import React from 'react';
import { ArrowRight, Terminal, Layers, Sparkles } from 'lucide-react';

interface HeroSectionProps {
  onApplyClick: () => void;
  onHowItWorksClick: () => void;
}

export const HeroSection: React.FC<HeroSectionProps> = ({
  onApplyClick,
  onHowItWorksClick
}) => {
  const ecosystemSteps = [
    { label: 'IDEA', desc: 'Brainstorm concepts' },
    { label: 'BUILD', desc: 'Form agile squads' },
    { label: 'COLLABORATE', desc: 'Complementary skills' },
    { label: 'PROJECT', desc: 'Deliver milestones' },
    { label: 'PROOF', desc: 'Verifiable record' }
  ];

  return (
    <section
      id="hero-section"
      className="relative overflow-hidden pt-12 pb-20 sm:pt-20 sm:pb-28 border-b border-[#1A1E29]"
    >
      {/* Subtle Background Glow */}
      <div className="absolute top-0 left-1/2 -translate-x-1/2 w-full max-w-5xl h-96 bg-gradient-to-b from-[#FFB020]/10 via-[#10B981]/5 to-transparent blur-3xl pointer-events-none" />

      <div className="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8 relative z-10">
        <div className="text-center space-y-6 max-w-4xl mx-auto">
          {/* Badge */}
          <div className="inline-flex items-center gap-2 px-3 py-1.5 rounded-full bg-[#181B24] border border-[#232736] text-xs font-mono text-[#DBDEE1]">
            <span className="w-2 h-2 rounded-full bg-[#10B981] animate-pulse" />
            <span>Private Professional Builder Guild</span>
            <span className="text-[#949BA4]">•</span>
            <span className="text-[#FFB020]">Discord-Based Community</span>
          </div>

          {/* Master Headline */}
          <h1 className="text-4xl sm:text-6xl lg:text-7xl font-black text-white tracking-tight leading-[1.08] uppercase font-mono">
            NO JOB.{' '}
            <span className="text-transparent bg-clip-text bg-gradient-to-r from-[#FFB020] via-[#FFD066] to-[#10B981]">
              STILL BUILDING.
            </span>
          </h1>

          {/* Supporting Text */}
          <p className="text-lg sm:text-xl text-[#DBDEE1] font-normal leading-relaxed max-w-3xl mx-auto">
            Jobless Empire is a private builder community for people who refuse to let unemployment become inactivity.
          </p>

          <p className="text-sm sm:text-base text-[#949BA4] leading-relaxed max-w-2xl mx-auto">
            Build ideas. Join projects. Collaborate with capable people. Work on legitimate opportunities. Leave behind proof of what you actually built.
          </p>

          {/* CTAs */}
          <div className="flex flex-col sm:flex-row items-center justify-center gap-3 pt-2">
            <button
              id="hero-primary-apply-btn"
              onClick={onApplyClick}
              className="w-full sm:w-auto px-7 py-3.5 rounded-xl bg-[#FFB020] hover:bg-[#E69900] text-black font-bold text-sm sm:text-base transition-all flex items-center justify-center gap-2 shadow-lg shadow-[#FFB020]/20 active:scale-98"
            >
              <span>Apply to Join</span>
              <ArrowRight className="w-4 h-4" />
            </button>
            <button
              id="hero-secondary-how-btn"
              onClick={onHowItWorksClick}
              className="w-full sm:w-auto px-7 py-3.5 rounded-xl bg-[#141620] hover:bg-[#1C202C] text-[#DBDEE1] hover:text-white border border-[#232736] font-semibold text-sm sm:text-base transition-all flex items-center justify-center gap-2"
            >
              <span>See How It Works</span>
              <Layers className="w-4 h-4 text-[#949BA4]" />
            </button>
          </div>

          {/* Ecosystem Flow Visual: IDEA -> BUILD -> COLLABORATE -> PROJECT -> PROOF */}
          <div className="pt-10 max-w-3xl mx-auto">
            <div className="bg-[#11131A] border border-[#232736] rounded-2xl p-4 sm:p-5 shadow-2xl">
              <div className="text-[11px] font-mono text-[#949BA4] uppercase tracking-wider mb-3 flex items-center justify-between">
                <span className="flex items-center gap-1.5">
                  <Terminal className="w-3.5 h-3.5 text-[#FFB020]" />
                  <span>The Operating Flow</span>
                </span>
                <span className="text-[#10B981]">Verifiable Proof Lifecycle</span>
              </div>

              <div className="grid grid-cols-5 gap-1 sm:gap-2">
                {ecosystemSteps.map((step, idx) => (
                  <div
                    key={step.label}
                    className="relative bg-[#181B24] border border-[#232736] rounded-lg p-2 sm:p-3 text-center transition-all hover:border-[#FFB020]/40 group"
                  >
                    <div className="text-[10px] sm:text-xs font-mono font-bold text-white group-hover:text-[#FFB020] transition-colors">
                      {step.label}
                    </div>
                    <div className="text-[9px] text-[#949BA4] hidden sm:block truncate mt-0.5">
                      {step.desc}
                    </div>
                    {idx < ecosystemSteps.length - 1 && (
                      <div className="hidden sm:block absolute -right-2 top-1/2 -translate-y-1/2 text-[#949BA4] text-xs z-10">
                        →
                      </div>
                    )}
                  </div>
                ))}
              </div>
            </div>
          </div>

          {/* Visual Anchor Quote */}
          <div className="pt-8">
            <div className="inline-block p-4 sm:p-5 rounded-xl bg-gradient-to-r from-[#181B24]/80 via-[#181B24] to-[#181B24]/80 border border-[#232736] max-w-2xl mx-auto">
              <div className="flex items-center justify-center gap-2 text-xs font-mono text-[#FFB020] mb-1">
                <Sparkles className="w-3.5 h-3.5" />
                <span>CORE ANCHOR</span>
              </div>
              <p className="text-base sm:text-xl font-bold text-white tracking-tight">
                “A job title can disappear. Your work doesn’t have to.”
              </p>
            </div>
          </div>
        </div>
      </div>
    </section>
  );
};
