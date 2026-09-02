import React, { useState, useRef } from 'react';
import { ShieldCheck, Upload, FileText, CheckCircle2, AlertCircle, ArrowRight, X } from 'lucide-react';
import { EmploymentStatusOption } from '../types';

export const ApplicationFormCard: React.FC = () => {
  const [fullName, setFullName] = useState('');
  const [email, setEmail] = useState('');
  const [employmentStatus, setEmploymentStatus] = useState<EmploymentStatusOption>('YES');
  const [resumeFile, setResumeFile] = useState<File | null>(null);
  const [consentAgreed, setConsentAgreed] = useState(false);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [isSubmitted, setIsSubmitted] = useState(false);
  const [errorMessage, setErrorMessage] = useState<string | null>(null);
  const fileInputRef = useRef<HTMLInputElement>(null);

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (file) {
      validateAndSetFile(file);
    }
  };

  const validateAndSetFile = (file: File) => {
    const validExtensions = ['pdf', 'doc', 'docx'];
    const extension = file.name.split('.').pop()?.toLowerCase();
    
    if (!extension || !validExtensions.includes(extension)) {
      setErrorMessage('Please upload a valid PDF, DOC, or DOCX resume.');
      return;
    }

    if (file.size > 10 * 1024 * 1024) {
      setErrorMessage('File size exceeds 10 MB limit.');
      return;
    }

    setErrorMessage(null);
    setResumeFile(file);
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setErrorMessage(null);

    if (!fullName.trim()) {
      setErrorMessage('Please enter your full name.');
      return;
    }

    if (!email.trim() || !email.includes('@')) {
      setErrorMessage('Please provide a valid communication email.');
      return;
    }

    if (!resumeFile) {
      setErrorMessage('Please attach your resume (PDF, DOC, or DOCX).');
      return;
    }

    if (!consentAgreed) {
      setErrorMessage('Please confirm that you understand the application terms.');
      return;
    }

    setIsSubmitting(true);

    try {
      // Read file as base64 for transmission and email attachment
      const fileBase64 = await new Promise<string>((resolve, reject) => {
        const reader = new FileReader();
        reader.onload = () => resolve(reader.result as string);
        reader.onerror = (error) => reject(error);
        reader.readAsDataURL(resumeFile);
      });

      const response = await fetch('/api/apply', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          fullName: fullName.trim(),
          email: email.trim(),
          employmentStatus,
          consentAgreed,
          resumeFile: {
            name: resumeFile.name,
            size: resumeFile.size,
            type: resumeFile.type,
            base64: fileBase64,
          },
        }),
      });

      const data = await response.json();

      if (!response.ok || !data.success) {
        throw new Error(data.error || 'Failed to submit application.');
      }

      setIsSubmitted(true);
    } catch (err: any) {
      console.error('Submission error:', err);
      // Even if network API encounters an edge case, provide smooth feedback
      setErrorMessage(err.message || 'Submission failed. Please try again.');
    } finally {
      setIsSubmitting(false);
    }
  };

  if (isSubmitted) {
    return (
      <section id="apply-section" className="py-16 sm:py-24 border-b border-[#1A1E29] bg-[#0B0D13]">
        <div className="max-w-2xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="bg-[#11131A] border border-[#10B981]/40 rounded-2xl p-8 sm:p-12 text-center space-y-6 shadow-2xl animate-fadeIn">
            <div className="w-14 h-14 rounded-2xl bg-[#10B981]/10 border border-[#10B981]/30 flex items-center justify-center text-[#10B981] mx-auto">
              <CheckCircle2 className="w-8 h-8" />
            </div>

            <div className="space-y-2">
              <span className="text-xs font-mono font-bold text-[#10B981] uppercase tracking-wider">
                ✓ SUBMISSION CONFIRMED
              </span>
              <h3 className="text-2xl sm:text-3xl font-black text-white font-mono uppercase">
                APPLICATION RECEIVED
              </h3>
            </div>

            <p className="text-sm sm:text-base text-[#DBDEE1] leading-relaxed">
              Thank you for applying to Jobless Empire. Your application has been submitted for manual review.
            </p>

            <div className="p-4 rounded-xl bg-[#181B24] border border-[#232736] text-xs text-[#949BA4] space-y-1 text-left font-mono">
              <div className="text-white font-bold">Applicant: {fullName}</div>
              <div>Email: {email}</div>
              <div>Status: Awaiting Manual Admin Review</div>
            </div>

            <p className="text-xs text-[#949BA4] leading-relaxed">
              If approved, we’ll send your personalized next steps to the email you provided.<br />
              No guarantees. No promises. Just an opportunity to keep building.
            </p>

            <div className="pt-2 text-sm font-mono text-[#FFB020] font-bold">
              Until then: Keep building.
            </div>
          </div>
        </div>
      </section>
    );
  }

  return (
    <section id="apply-section" className="py-16 sm:py-24 border-b border-[#1A1E29] bg-[#0B0D13]">
      <div className="max-w-3xl mx-auto px-4 sm:px-6 lg:px-8 space-y-10">
        
        {/* Header */}
        <div className="text-center space-y-4">
          <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-[#181B24] border border-[#232736] text-xs font-mono text-[#FFB020]">
            <ShieldCheck className="w-3.5 h-3.5" />
            <span>13 • APPLICATION</span>
          </div>
          <h2 className="text-3xl sm:text-5xl font-black text-white font-mono tracking-tight uppercase">
            READY TO KEEP BUILDING?
          </h2>
          <p className="text-sm sm:text-base text-[#DBDEE1]">
            If the idea resonates with you, tell us who you are, what you can contribute, and where you’re trying to go.
          </p>
        </div>

        {/* Form Card */}
        <div className="bg-[#11131A] border border-[#232736] rounded-2xl p-6 sm:p-10 shadow-2xl">
          <form onSubmit={handleSubmit} className="space-y-6">
            
            {/* Error Message */}
            {errorMessage && (
              <div className="p-4 rounded-xl bg-[#F43F5E]/10 border border-[#F43F5E]/30 flex items-center gap-2 text-xs text-[#F43F5E] font-mono">
                <AlertCircle className="w-4 h-4 shrink-0" />
                <span>{errorMessage}</span>
              </div>
            )}

            {/* Full Name */}
            <div className="space-y-2">
              <label className="text-xs font-mono font-bold text-white uppercase flex items-center justify-between">
                <span>Full Name *</span>
                <span className="text-[#949BA4] font-normal text-[10px]">Required</span>
              </label>
              <input
                type="text"
                required
                placeholder="e.g. Alex Vance"
                value={fullName}
                onChange={(e) => setFullName(e.target.value)}
                className="w-full bg-[#181B24] border border-[#232736] rounded-xl px-4 py-3 text-sm text-white placeholder-[#949BA4] focus:outline-none focus:border-[#FFB020] transition-colors"
              />
            </div>

            {/* Email */}
            <div className="space-y-2">
              <label className="text-xs font-mono font-bold text-white uppercase flex items-center justify-between">
                <span>Best Communication Email *</span>
                <span className="text-[#949BA4] font-normal text-[10px]">For personalized invite</span>
              </label>
              <input
                type="email"
                required
                placeholder="alex.vance@example.com"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                className="w-full bg-[#181B24] border border-[#232736] rounded-xl px-4 py-3 text-sm text-white placeholder-[#949BA4] focus:outline-none focus:border-[#FFB020] transition-colors"
              />
            </div>

            {/* Resume Upload */}
            <div className="space-y-2">
              <label className="text-xs font-mono font-bold text-white uppercase flex items-center justify-between">
                <span>Resume / Background Document *</span>
                <span className="text-[#949BA4] font-normal text-[10px]">PDF, DOC, DOCX (Max 10 MB)</span>
              </label>

              <input
                type="file"
                ref={fileInputRef}
                accept=".pdf,.doc,.docx"
                onChange={handleFileChange}
                className="hidden"
              />

              {!resumeFile ? (
                <div
                  onClick={() => fileInputRef.current?.click()}
                  className="border-2 border-dashed border-[#232736] hover:border-[#FFB020]/60 rounded-xl p-6 text-center cursor-pointer transition-colors bg-[#181B24]/40 group"
                >
                  <Upload className="w-6 h-6 text-[#949BA4] group-hover:text-[#FFB020] mx-auto mb-2 transition-colors" />
                  <div className="text-xs font-medium text-white">Click or drag & drop to upload resume</div>
                  <div className="text-[10px] text-[#949BA4] mt-0.5">PDF, DOC, DOCX up to 10 MB</div>
                </div>
              ) : (
                <div className="bg-[#181B24] border border-[#10B981]/40 rounded-xl p-3 flex items-center justify-between">
                  <div className="flex items-center gap-2">
                    <FileText className="w-5 h-5 text-[#10B981]" />
                    <div>
                      <div className="text-xs font-bold text-white">{resumeFile.name}</div>
                      <div className="text-[10px] text-[#949BA4]">
                        {(resumeFile.size / 1024).toFixed(1)} KB
                      </div>
                    </div>
                  </div>
                  <button
                    type="button"
                    onClick={() => setResumeFile(null)}
                    className="p-1 rounded text-[#949BA4] hover:text-white hover:bg-[#232736]"
                  >
                    <X className="w-4 h-4" />
                  </button>
                </div>
              )}
            </div>

            {/* Employment Status Radio */}
            <div className="space-y-2">
              <label className="text-xs font-mono font-bold text-white uppercase">
                Are you currently jobless / unemployed? *
              </label>
              <div className="grid grid-cols-1 sm:grid-cols-3 gap-2">
                {[
                  { value: 'YES', label: 'Yes, between roles' },
                  { value: 'NO', label: 'No, but building' },
                  { value: 'PREFER_NOT_TO_SAY', label: 'Prefer not to say' }
                ].map((opt) => (
                  <label
                    key={opt.value}
                    className={`flex items-center gap-2.5 p-3 rounded-xl border text-xs cursor-pointer transition-all ${
                      employmentStatus === opt.value
                        ? 'bg-[#181B24] border-[#FFB020] text-white font-bold'
                        : 'bg-[#11131A] border-[#232736] text-[#949BA4] hover:text-white'
                    }`}
                  >
                    <input
                      type="radio"
                      name="employmentStatus"
                      value={opt.value}
                      checked={employmentStatus === opt.value}
                      onChange={() => setEmploymentStatus(opt.value as EmploymentStatusOption)}
                      className="accent-[#FFB020]"
                    />
                    <span>{opt.label}</span>
                  </label>
                ))}
              </div>
            </div>

            {/* Mandatory Consent Checkbox */}
            <div className="pt-2">
              <label className="flex items-start gap-3 cursor-pointer select-none">
                <input
                  type="checkbox"
                  required
                  checked={consentAgreed}
                  onChange={(e) => setConsentAgreed(e.target.checked)}
                  className="mt-1 accent-[#FFB020] w-4 h-4 rounded cursor-pointer shrink-0"
                />
                <span className="text-xs text-[#DBDEE1] leading-relaxed">
                  I understand that applying does not guarantee membership, employment, project selection, client work, or income. I agree to the Jobless Empire community expectations and understand that applications are manually reviewed.
                </span>
              </label>
            </div>

            {/* Submit Button */}
            <div className="pt-4">
              <button
                type="submit"
                disabled={isSubmitting}
                className="w-full py-4 rounded-xl bg-[#FFB020] hover:bg-[#E69900] disabled:bg-[#232736] text-black font-black text-sm uppercase tracking-wider font-mono transition-all flex items-center justify-center gap-2 shadow-lg shadow-[#FFB020]/20 active:scale-98"
              >
                {isSubmitting ? (
                  <span>Submitting for Review...</span>
                ) : (
                  <>
                    <span>Submit Application for Manual Review</span>
                    <ArrowRight className="w-4 h-4" />
                  </>
                )}
              </button>
            </div>

            <p className="text-[11px] text-center text-[#949BA4] font-mono">
              Privacy Protected • No Public Server Links • Single-Use Invitations
            </p>

          </form>
        </div>

      </div>
    </section>
  );
};
