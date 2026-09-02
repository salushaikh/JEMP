import React from 'react';
import { ArrowRight, Sparkles, Layers, ShieldCheck } from 'lucide-react';
import { NavSection } from '../types';

interface FinalCtaFooterProps {
  onNavClick: (section: NavSection) => void;
}

export const FinalCtaFooter: React.FC<FinalCtaFooterProps> = ({ onNavClick }) => {
  return (
    <footer id="final-cta-footer-section" className="bg-[#08090D] border-t border-[#1A1E29] pt-20 pb-12">
      <div className="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8 space-y-20">
        
        {/* Final Philosophy & CTA Banner */}
        <div className="bg-gradient-to-b from-[#11131A] to-[#0D0F15] border border-[#232736] rounded-3xl p-8 sm:p-14 text-center space-y-8 shadow-2xl relative overflow-hidden">
          <div className="absolute top-0 left-1/2 -translate-x-1/2 w-96 h-48 bg-[#FFB020]/10 blur-3xl pointer-events-none" />

          <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-[#181B24] border border-[#232736] text-xs font-mono text-[#FFB020]">
            <Sparkles className="w-3.5 h-3.5" />
            <span>THE CLOSING PHILOSOPHY</span>
          </div>

          <div className="space-y-4 max-w-3xl mx-auto">
            <h2 className="text-2xl sm:text-4xl font-black text-white font-mono tracking-tight uppercase leading-tight">
              A JOB CAN TELL PEOPLE WHAT YOU WERE HIRED TO DO.<br />
              <span className="text-transparent bg-clip-text bg-gradient-to-r from-[#FFB020] to-[#10B981]">
                YOUR WORK CAN SHOW THEM WHAT YOU’RE CAPABLE OF.
              </span>
            </h2>
            <p className="text-sm sm:text-base text-[#DBDEE1]">
              Jobless Empire exists to help you keep producing that evidence.
            </p>
          </div>

          <div className="p-4 rounded-xl bg-[#181B24]/90 border border-[#232736] max-w-xl mx-auto space-y-1">
            <p className="text-xs sm:text-sm font-bold text-white">
              “Your current employment status is a situation. It doesn’t have to be your identity.”
            </p>
            <p className="text-xs text-[#FFB020] font-mono font-bold">
              Build something.
            </p>
          </div>

          {/* Action buttons */}
          <div className="flex flex-col sm:flex-row items-center justify-center gap-3 pt-2">
            <button
              onClick={() => onNavClick(NavSection.APPLY)}
              className="w-full sm:w-auto px-8 py-4 rounded-xl bg-[#FFB020] hover:bg-[#E69900] text-black font-black text-sm uppercase tracking-wider font-mono transition-all flex items-center justify-center gap-2 shadow-xl shadow-[#FFB020]/20 active:scale-98"
            >
              <span>Apply to Join Jobless Empire</span>
              <ArrowRight className="w-4 h-4" />
            </button>
            <button
              onClick={() => onNavClick(NavSection.HOW_IT_WORKS)}
              className="w-full sm:w-auto px-8 py-4 rounded-xl bg-[#181B24] hover:bg-[#232736] text-[#DBDEE1] hover:text-white border border-[#232736] font-semibold text-sm transition-all flex items-center justify-center gap-2"
            >
              <span>Explore How It Works</span>
              <Layers className="w-4 h-4 text-[#949BA4]" />
            </button>
          </div>
        </div>

        {/* Footer Navigation & Legal */}
        <div className="grid grid-cols-1 md:grid-cols-4 gap-8 pt-8 border-t border-[#1A1E29] text-xs text-[#949BA4]">
          {/* Brand Info */}
          <div className="space-y-3 md:col-span-2">
            <div className="flex items-center gap-2 text-white font-mono font-black text-base">
              <div className="w-6 h-6 rounded bg-[#FFB020] flex items-center justify-center text-black text-xs">
                JE
              </div>
              <span>JOBLESS EMPIRE</span>
            </div>
            <p className="text-xs text-[#DBDEE1] font-mono">
              No job. Still building.
            </p>
            <p className="text-xs text-[#949BA4] max-w-sm leading-relaxed">
              Private professional builder community for people between jobs to collaborate on real projects, verify proof-of-work, and maintain momentum.
            </p>
          </div>

          {/* Nav Links */}
          <div className="space-y-2">
            <div className="font-mono font-bold text-white uppercase text-[11px]">
              Navigation
            </div>
            <ul className="space-y-1.5 font-medium">
              <li>
                <button
                  onClick={() => onNavClick(NavSection.HOW_IT_WORKS)}
                  className="hover:text-white transition-colors"
                >
                  How It Works
                </button>
              </li>
              <li>
                <button
                  onClick={() => onNavClick(NavSection.WORKSPACES)}
                  className="hover:text-white transition-colors"
                >
                  Workspaces (B#, P#, HC#)
                </button>
              </li>
              <li>
                <button
                  onClick={() => onNavClick(NavSection.PROOF_OF_WORK)}
                  className="hover:text-white transition-colors"
                >
                  Proof of Work Timeline
                </button>
              </li>
              <li>
                <button
                  onClick={() => onNavClick(NavSection.TRANSPARENCY)}
                  className="hover:text-white transition-colors"
                >
                  Financial Transparency
                </button>
              </li>
              <li>
                <button
                  onClick={() => onNavClick(NavSection.FAQ)}
                  className="hover:text-white transition-colors"
                >
                  FAQ
                </button>
              </li>
            </ul>
          </div>

          {/* Community Protocols */}
          <div className="space-y-2">
            <div className="font-mono font-bold text-white uppercase text-[11px]">
              Community Protocols
            </div>
            <div className="space-y-1.5 text-[11px] leading-relaxed">
              <div className="flex items-center gap-1.5 text-[#10B981]">
                <ShieldCheck className="w-3.5 h-3.5" />
                <span>Manual Application Review</span>
              </div>
              <div>Single-Use Expiring Invites</div>
              <div>5% Admin / 95% Squad Payout</div>
              <div>Zero Tolerance for Scams</div>
            </div>
          </div>
        </div>

        {/* Bottom Disclaimer */}
        <div className="pt-6 border-t border-[#141620] flex flex-col sm:flex-row items-center justify-between text-[11px] text-[#949BA4] gap-3 font-mono">
          <div>© {new Date().getFullYear()} Jobless Empire. All rights reserved.</div>
          <div className="text-center sm:text-right">
            Private builder network • No guarantee of employment or income.
          </div>
        </div>

      </div>
    </footer>
  );
};
