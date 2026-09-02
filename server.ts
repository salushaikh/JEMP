import express from "express";
import path from "path";
import dotenv from "dotenv";
import nodemailer from "nodemailer";

dotenv.config();

const app = express();
const PORT = process.env.PORT || 3000;

// Support JSON payloads up to 15MB for resume attachments
app.use(express.json({ limit: "15mb" }));
app.use(express.urlencoded({ extended: true, limit: "15mb" }));

// In-memory store for received applications
export interface StoredApplication {
  id: string;
  fullName: string;
  email: string;
  employmentStatus: string;
  resumeFileName?: string;
  resumeFileSize?: number;
  submittedAt: string;
  emailDispatched: boolean;
  deliveryMethod?: "RESEND_API" | "SENDGRID_API" | "WEBHOOK" | "SMTP" | "STORED_LOCALLY";
  notes?: string;
}

const applicationsStore: StoredApplication[] = [];

// Helper function to create Nodemailer transport with strict connection timeouts to prevent hanging
function createMailTransporter() {
  const host = process.env.SMTP_HOST?.trim();
  const port = parseInt(process.env.SMTP_PORT?.trim() || "587", 10);
  const user = process.env.SMTP_USER?.trim();
  const pass = process.env.SMTP_PASS?.trim();
  const secure = process.env.SMTP_SECURE === "true" || port === 465;

  if (!host || !user || !pass) {
    return null;
  }

  return nodemailer.createTransport({
    host,
    port,
    secure,
    auth: {
      user,
      pass,
    },
    // Set strict connection timeouts to prevent server hanging if cloud environment blocks raw SMTP ports
    connectionTimeout: 6000,
    greetingTimeout: 6000,
    socketTimeout: 10000,
    tls: {
      rejectUnauthorized: false,
    },
  });
}

/**
 * Dispatch application email using available HTTP APIs (Resend, SendGrid), Webhooks, or SMTP.
 * HTTP APIs over port 443 bypass Cloud Run / container SMTP socket restrictions.
 */
async function dispatchApplicationNotification(payload: {
  submissionId: string;
  fullName: string;
  email: string;
  employmentStatus: string;
  submissionTime: string;
  resumeFile?: { name: string; size: number; base64: string; type?: string };
}): Promise<{ success: boolean; method: StoredApplication["deliveryMethod"]; error?: string }> {
  const destinationEmail =
    process.env.APPLICATION_EMAIL_TO?.trim() ||
    process.env.ADMIN_EMAIL?.trim() ||
    "salehasshaikh82@gmail.com";

  const statusLabel =
    payload.employmentStatus === "YES"
      ? "Yes, currently between roles"
      : payload.employmentStatus === "NO"
      ? "No, but building"
      : "Prefer not to say";

  const emailSubject = `[Jobless Empire] New Application: ${payload.fullName} (${payload.employmentStatus})`;

  const emailHtml = `
    <div style="font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif; max-width: 600px; margin: 0 auto; background-color: #0E1017; color: #DBDEE1; border: 1px solid #232736; border-radius: 12px; overflow: hidden;">
      <div style="background-color: #11131A; padding: 24px; border-bottom: 1px solid #232736;">
        <h2 style="margin: 0; color: #FFB020; font-family: monospace; font-size: 20px; text-transform: uppercase;">
          ⚡ New Jobless Empire Application
        </h2>
        <p style="margin: 4px 0 0 0; color: #949BA4; font-size: 13px;">
          Application ID: ${payload.submissionId} • Submitted: ${new Date(payload.submissionTime).toLocaleString()}
        </p>
      </div>
      
      <div style="padding: 24px; font-size: 14px; line-height: 1.6;">
        <div style="margin-bottom: 16px;">
          <strong style="color: #FFFFFF; display: block; margin-bottom: 4px;">Candidate Full Name:</strong>
          <span style="color: #10B981; font-weight: bold; font-size: 16px;">${payload.fullName}</span>
        </div>

        <div style="margin-bottom: 16px;">
          <strong style="color: #FFFFFF; display: block; margin-bottom: 4px;">Candidate Email:</strong>
          <a href="mailto:${payload.email}" style="color: #38BDF8; text-decoration: none;">${payload.email}</a>
        </div>

        <div style="margin-bottom: 16px;">
          <strong style="color: #FFFFFF; display: block; margin-bottom: 4px;">Employment Status:</strong>
          <span style="background-color: #181B24; border: 1px solid #232736; padding: 4px 10px; border-radius: 6px; font-family: monospace; font-size: 12px; color: #FFFFFF;">
            ${statusLabel}
          </span>
        </div>

        ${
          payload.resumeFile?.name
            ? `
        <div style="margin-bottom: 8px;">
          <strong style="color: #FFFFFF; display: block; margin-bottom: 4px;">Resume File Attached:</strong>
          <span style="color: #DBDEE1;">📎 ${payload.resumeFile.name} (${(payload.resumeFile.size / 1024).toFixed(1)} KB)</span>
        </div>
        `
            : ""
        }
      </div>

      <div style="background-color: #08090D; padding: 16px 24px; border-top: 1px solid #232736; font-size: 11px; color: #949BA4; text-align: center; font-family: monospace;">
        Jobless Empire • Automated Application Notification System
      </div>
    </div>
  `;

  // 1. Check HTTP-based Resend API (Recommended for Cloud / Serverless)
  if (process.env.RESEND_API_KEY) {
    try {
      const attachments = [];
      if (payload.resumeFile?.base64 && payload.resumeFile?.name) {
        const base64Data = payload.resumeFile.base64.replace(/^data:.*?;base64,/, "");
        attachments.push({
          filename: payload.resumeFile.name,
          content: base64Data,
        });
      }

      const res = await fetch("https://api.resend.com/emails", {
        method: "POST",
        headers: {
          Authorization: `Bearer ${process.env.RESEND_API_KEY.trim()}`,
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          from: process.env.EMAIL_FROM || "Jobless Empire <onboarding@resend.dev>",
          to: [destinationEmail],
          reply_to: payload.email,
          subject: emailSubject,
          html: emailHtml,
          attachments: attachments.length > 0 ? attachments : undefined,
        }),
      });

      if (res.ok) {
        console.log(`[Jobless Empire] Notification sent via Resend HTTP API to ${destinationEmail}`);
        return { success: true, method: "RESEND_API" };
      } else {
        const errText = await res.text();
        console.warn(`[Jobless Empire] Resend API error: ${errText}`);
      }
    } catch (err: any) {
      console.warn(`[Jobless Empire] Resend API dispatch failed:`, err.message);
    }
  }

  // 2. Check HTTP-based SendGrid API
  if (process.env.SENDGRID_API_KEY) {
    try {
      const attachments = [];
      if (payload.resumeFile?.base64 && payload.resumeFile?.name) {
        const base64Data = payload.resumeFile.base64.replace(/^data:.*?;base64,/, "");
        attachments.push({
          content: base64Data,
          filename: payload.resumeFile.name,
          type: payload.resumeFile.type || "application/octet-stream",
          disposition: "attachment",
        });
      }

      const res = await fetch("https://api.sendgrid.com/v3/mail/send", {
        method: "POST",
        headers: {
          Authorization: `Bearer ${process.env.SENDGRID_API_KEY.trim()}`,
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          personalizations: [{ to: [{ email: destinationEmail }] }],
          from: {
            email: process.env.SENDGRID_FROM_EMAIL || "notifications@joblessempire.com",
            name: "Jobless Empire Admissions",
          },
          reply_to: { email: payload.email, name: payload.fullName },
          subject: emailSubject,
          content: [{ type: "text/html", value: emailHtml }],
          attachments: attachments.length > 0 ? attachments : undefined,
        }),
      });

      if (res.ok) {
        console.log(`[Jobless Empire] Notification sent via SendGrid Web API to ${destinationEmail}`);
        return { success: true, method: "SENDGRID_API" };
      } else {
        const errText = await res.text();
        console.warn(`[Jobless Empire] SendGrid API error: ${errText}`);
      }
    } catch (err: any) {
      console.warn(`[Jobless Empire] SendGrid dispatch failed:`, err.message);
    }
  }

  // 3. Check Webhook notification (e.g. Discord, Slack, Zapier, Make)
  if (process.env.APPLICATION_WEBHOOK_URL) {
    try {
      const webhookUrl = process.env.APPLICATION_WEBHOOK_URL.trim();
      const webhookPayload = webhookUrl.includes("discord.com")
        ? {
            content: `📢 **New Jobless Empire Application Received!**\n**Candidate:** ${payload.fullName} (<${payload.email}>)\n**Status:** ${statusLabel}\n**Resume:** ${payload.resumeFile?.name || "None"}\n**ID:** \`${payload.submissionId}\``,
          }
        : {
            text: `New Jobless Empire Application from ${payload.fullName} (${payload.email})`,
            data: payload,
          };

      const res = await fetch(webhookUrl, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(webhookPayload),
      });

      if (res.ok) {
        console.log(`[Jobless Empire] Webhook dispatched to ${webhookUrl}`);
        return { success: true, method: "WEBHOOK" };
      }
    } catch (err: any) {
      console.warn(`[Jobless Empire] Webhook dispatch failed:`, err.message);
    }
  }

  // 4. Try SMTP with strict timeouts (catches connection timeout and handles gracefully)
  const transporter = createMailTransporter();
  if (transporter) {
    try {
      const attachments = [];
      if (payload.resumeFile?.base64 && payload.resumeFile?.name) {
        const base64Data = payload.resumeFile.base64.replace(/^data:.*?;base64,/, "");
        attachments.push({
          filename: payload.resumeFile.name,
          content: Buffer.from(base64Data, "base64"),
        });
      }

      // Determine exact sender email for SMTP
      const rawUser = process.env.SMTP_USER?.trim() || "";
      const rawFrom = process.env.EMAIL_FROM?.trim() || "";
      
      // Extract clean email address
      let senderEmail = rawUser;
      if (rawFrom.includes("<") && rawFrom.includes(">")) {
        senderEmail = rawFrom.split("<")[1].split(">")[0].trim();
      } else if (rawFrom) {
        senderEmail = rawFrom;
      }

      // If user provided a custom display name in EMAIL_FROM or default to Jobless Empire
      const fromHeader = rawFrom || (rawUser ? `"Jobless Empire Admissions" <${rawUser}>` : `"Jobless Empire Admissions" <notifications@joblessempire.com>`);

      await transporter.sendMail({
        from: fromHeader,
        to: destinationEmail,
        replyTo: payload.email,
        envelope: senderEmail ? {
          from: senderEmail,
          to: destinationEmail,
        } : undefined,
        subject: emailSubject,
        html: emailHtml,
        attachments,
      });

      console.log(`[Jobless Empire] Application email successfully sent to ${destinationEmail} via SMTP (From: ${senderEmail || fromHeader}).`);
      return { success: true, method: "SMTP" };
    } catch (err: any) {
      const errMsg = err.message || String(err);
      console.error(`[Jobless Empire] SMTP delivery encountered error/timeout:`, errMsg);

      if (errMsg.includes("550") && errMsg.includes("Sender verify failed")) {
        console.error(
          `[Jobless Empire Tip] 550 Sender verify failed means your mail server requires the sender email address to be an active, existing mailbox on your domain. Ensure SMTP_USER or EMAIL_FROM is set to a real email address (e.g. your active email on that domain).`
        );
      }

      return {
        success: false,
        method: "STORED_LOCALLY",
        error: `SMTP issue: ${errMsg}. Application stored in dashboard.`,
      };
    }
  }

  // Fallback: Stored in memory / server queue
  console.log(`[Jobless Empire] Application ${payload.submissionId} recorded in internal review queue.`);
  return {
    success: false,
    method: "STORED_LOCALLY",
    error: "No active email service configured. Submission recorded in admin store.",
  };
}

// API: Health check
app.get("/api/health", (req, res) => {
  res.json({
    status: "ok",
    timestamp: new Date().toISOString(),
    name: "Jobless Empire API",
    storedSubmissionsCount: applicationsStore.length,
  });
});

// API: Check email configuration status & review submissions
app.get("/api/admin/status", (req, res) => {
  const hasResend = Boolean(process.env.RESEND_API_KEY);
  const hasSendGrid = Boolean(process.env.SENDGRID_API_KEY);
  const hasWebhook = Boolean(process.env.APPLICATION_WEBHOOK_URL);
  const hasSmtp = Boolean(process.env.SMTP_HOST && process.env.SMTP_USER && process.env.SMTP_PASS);

  const destinationEmail =
    process.env.APPLICATION_EMAIL_TO ||
    process.env.ADMIN_EMAIL ||
    "salehasshaikh82@gmail.com";

  res.json({
    destinationEmail,
    deliveryChannels: {
      resendApi: hasResend,
      sendgridApi: hasSendGrid,
      webhook: hasWebhook,
      smtp: hasSmtp,
    },
    isEmailConfigured: hasResend || hasSendGrid || hasWebhook || hasSmtp,
    totalSubmissions: applicationsStore.length,
    recentSubmissions: applicationsStore.slice(0, 10).map((app) => ({
      id: app.id,
      fullName: app.fullName,
      email: app.email,
      employmentStatus: app.employmentStatus,
      resumeFileName: app.resumeFileName,
      submittedAt: app.submittedAt,
      emailDispatched: app.emailDispatched,
      deliveryMethod: app.deliveryMethod,
    })),
  });
});

// API: Form Submission Handler
app.post("/api/apply", async (req, res) => {
  try {
    const { fullName, email, employmentStatus, consentAgreed, resumeFile } = req.body;

    if (!fullName || !email) {
      return res.status(400).json({
        success: false,
        error: "Full name and email are required.",
      });
    }

    if (!consentAgreed) {
      return res.status(400).json({
        success: false,
        error: "Consent agreement is required.",
      });
    }

    const submissionId = `APP-${Date.now()}-${Math.floor(Math.random() * 1000)}`;
    const submissionTime = new Date().toISOString();

    // Dispatch email notification safely (with timeout protection and multi-channel support)
    const dispatchResult = await dispatchApplicationNotification({
      submissionId,
      fullName,
      email,
      employmentStatus,
      submissionTime,
      resumeFile,
    });

    // Save submission to memory store so it is NEVER lost regardless of network conditions
    const storedApp: StoredApplication = {
      id: submissionId,
      fullName,
      email,
      employmentStatus,
      resumeFileName: resumeFile?.name,
      resumeFileSize: resumeFile?.size,
      submittedAt: submissionTime,
      emailDispatched: dispatchResult.success,
      deliveryMethod: dispatchResult.method,
      notes: dispatchResult.error,
    };
    applicationsStore.unshift(storedApp);

    return res.status(200).json({
      success: true,
      submissionId,
      emailDispatched: dispatchResult.success,
      deliveryMethod: dispatchResult.method,
      message: "Application submitted successfully and registered for review.",
    });
  } catch (error: any) {
    console.error("[Jobless Empire] Server error handling application:", error);
    return res.status(500).json({
      success: false,
      error: "Internal server error processing application.",
    });
  }
});

// Vite middleware in dev or static serving in production
async function startServer() {
  if (process.env.NODE_ENV !== "production") {
    const { createServer: createViteServer } = await import("vite");
    const vite = await createViteServer({
      server: { middlewareMode: true },
      appType: "spa",
    });
    app.use(vite.middlewares);
  } else {
    const distPath = path.join(process.cwd(), "dist");
    app.use(express.static(distPath));
    app.get("*", (req, res) => {
      res.sendFile(path.join(distPath, "index.html"));
    });
  }

  if (typeof PORT === "string" && isNaN(Number(PORT))) {
    // Passenger passes a Unix domain socket path
    app.listen(PORT, () => {
      console.log(`Jobless Empire server running on Passenger socket ${PORT}`);
    });
  } else {
    const portNumber = Number(PORT) || 3000;
    app.listen(portNumber, "0.0.0.0", () => {
      console.log(`Jobless Empire server running on port ${portNumber}`);
    });
  }
}

startServer();
