import React, { useState } from 'react';
import { ShieldCheck, Download, Copy, Check, Lock, FileCode, FileText } from 'lucide-react';
import { DEMO_MEMBER_PROFILE } from '../data/joblessData';

export const ProofOfWorkTimelineSection: React.FC = () => {
  const [copiedFormat, setCopiedFormat] = useState<string | null>(null);
  const profile = DEMO_MEMBER_PROFILE;

  const handleExport = (format: string) => {
    let content = '';
    if (format === 'MARKDOWN') {
      content = `# JOBLESS EMPIRE — PROOF OF WORK TIMELINE
**Member:** ${profile.name} (${profile.id})
**Role:** ${profile.role}
**Reputation:** ${profile.reputationScore}/100
**Verified Deliverables:** ${profile.timeline.length}

---
${profile.timeline.map((t, i) => `### ${i + 1}. [${t.date}] ${t.title}\n- **Category:** ${t.category}\n- **Ref:** ${t.projectRef}\n- **Summary:** ${t.description}\n- **Hash:** \`${t.verificationHash}\`\n`).join('\n')}`;
    } else if (format === 'JSON') {
      content = JSON.stringify(profile, null, 2);
    } else {
      content = `Jobless Empire Verified Proof Summary for ${profile.name} (${profile.id})`;
    }

    navigator.clipboard.writeText(content);
    setCopiedFormat(format);
    setTimeout(() => setCopiedFormat(null), 2000);
  };

  return (
    <section id="proof-of-work-section" className="py-16 sm:py-24 border-b border-[#1A1E29] bg-[#0E1017]">
      <div className="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8 space-y-16">
        
        {/* Header */}
        <div className="text-center max-w-3xl mx-auto space-y-4">
          <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-[#181B24] border border-[#232736] text-xs font-mono text-[#10B981]">
            <span>07 • PROOF-OF-WORK TIMELINE</span>
          </div>
          <h2 className="text-3xl sm:text-5xl font-black text-white font-mono tracking-tight uppercase">
            BUILD A RECORD, NOT JUST A RESUME.
          </h2>
          <p className="text-base sm:text-lg text-[#DBDEE1]">
            Every verified member receives a permanent <span className="text-[#10B981] font-mono font-bold">JEMP#</span> identity. Contributions, milestones, and project completions create a private proof-of-work record.
          </p>
        </div>

        {/* Timeline Privacy & Identity Banner */}
        <div className="bg-[#11131A] border border-[#232736] rounded-2xl p-6 sm:p-8 space-y-6">
          <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
            <div className="p-4 rounded-xl bg-[#181B24] border border-[#232736] space-y-1">
              <div className="flex items-center gap-2 text-xs font-mono text-[#10B981] font-bold">
                <ShieldCheck className="w-4 h-4" />
                <span>PERMANENT JEMP# IDENTITY</span>
              </div>
              <div className="text-xl font-bold font-mono text-white">{profile.id}</div>
              <p className="text-xs text-[#949BA4]">Assigned after manual review & onboarding.</p>
            </div>

            <div className="p-4 rounded-xl bg-[#181B24] border border-[#232736] space-y-1">
              <div className="flex items-center gap-2 text-xs font-mono text-[#FFB020] font-bold">
                <Lock className="w-4 h-4" />
                <span>PRIVATE BY DEFAULT</span>
              </div>
              <div className="text-base font-bold text-white">Full Member Privacy Control</div>
              <p className="text-xs text-[#949BA4]">You decide what to share, export, or keep private.</p>
            </div>

            <div className="p-4 rounded-xl bg-[#181B24] border border-[#232736] space-y-1">
              <div className="flex items-center gap-2 text-xs font-mono text-[#38BDF8] font-bold">
                <Download className="w-4 h-4" />
                <span>EXPORT READY</span>
              </div>
              <div className="text-base font-bold text-white">PDF • Markdown • JSON • DOCX</div>
              <p className="text-xs text-[#949BA4]">Embed verified artifacts into resumes and portfolios.</p>
            </div>
          </div>

          {/* Interactive Timeline Stream (Illustrative Demo) */}
          <div className="space-y-3">
            <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-2 pb-2 border-b border-[#232736]">
              <span className="text-xs font-mono font-bold text-[#949BA4] uppercase">
                VERIFIED TIMELINE FEED (DEMO PROFILE: {profile.id}):
              </span>
              <div className="flex items-center gap-2">
                <button
                  onClick={() => handleExport('MARKDOWN')}
                  className="px-2.5 py-1 rounded bg-[#181B24] hover:bg-[#232736] text-xs font-mono text-[#DBDEE1] border border-[#232736] flex items-center gap-1 transition-all"
                >
                  {copiedFormat === 'MARKDOWN' ? <Check className="w-3.5 h-3.5 text-[#10B981]" /> : <FileText className="w-3.5 h-3.5" />}
                  <span>Copy Markdown</span>
                </button>
                <button
                  onClick={() => handleExport('JSON')}
                  className="px-2.5 py-1 rounded bg-[#181B24] hover:bg-[#232736] text-xs font-mono text-[#DBDEE1] border border-[#232736] flex items-center gap-1 transition-all"
                >
                  {copiedFormat === 'JSON' ? <Check className="w-3.5 h-3.5 text-[#10B981]" /> : <FileCode className="w-3.5 h-3.5" />}
                  <span>Copy JSON</span>
                </button>
              </div>
            </div>

            <div className="space-y-3">
              {profile.timeline.map((entry) => (
                <div
                  key={entry.id}
                  className="p-4 rounded-xl bg-[#181B24] border border-[#232736] space-y-2 hover:border-[#10B981]/40 transition-all"
                >
                  <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-1">
                    <div className="flex items-center gap-2">
                      <span className="font-mono text-xs text-[#10B981] font-bold">
                        {entry.date || entry.timestamp}
                      </span>
                      <span className="text-white font-bold text-sm">{entry.title}</span>
                    </div>
                    <div className="flex items-center gap-2">
                      {entry.projectRef && (
                        <span className="px-2 py-0.5 rounded bg-[#38BDF8]/20 text-[#38BDF8] font-mono text-[10px] font-bold">
                          {entry.projectRef}
                        </span>
                      )}
                      <span className="px-2 py-0.5 rounded bg-[#11131A] text-[#949BA4] font-mono text-[10px] border border-[#232736]">
                        {entry.category}
                      </span>
                    </div>
                  </div>

                  <p className="text-xs text-[#DBDEE1] leading-relaxed">{entry.description}</p>

                  <div className="pt-2 border-t border-[#232736] flex flex-col sm:flex-row items-start sm:items-center justify-between text-[10px] font-mono text-[#949BA4] gap-1">
                    <div className="flex items-center gap-1.5 truncate max-w-md">
                      <span className="text-[#10B981]">ATTESTATION HASH:</span>
                      <span className="text-[#DBDEE1] truncate">{entry.verificationHash}</span>
                    </div>
                    {entry.verifiedBy && (
                      <span className="text-[#949BA4]">Verified by: {entry.verifiedBy}</span>
                    )}
                  </div>
                </div>
              ))}
            </div>
          </div>
        </div>

      </div>
    </section>
  );
};
