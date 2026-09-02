import React from 'react';
import { Layers, GitFork, ArrowDown, CheckCircle, ShieldCheck } from 'lucide-react';

export const EcosystemDiagramsSection: React.FC = () => {
  return (
    <section id="ecosystem-diagrams-section" className="py-16 sm:py-24 border-b border-[#1A1E29] bg-[#0B0D13]">
      <div className="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8 space-y-16">
        
        {/* Header */}
        <div className="text-center max-w-3xl mx-auto space-y-4">
          <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-[#181B24] border border-[#232736] text-xs font-mono text-[#FFB020]">
            <span>08 • CONNECTED SYSTEM ARCHITECTURE</span>
          </div>
          <h2 className="text-3xl sm:text-5xl font-black text-white font-mono tracking-tight uppercase">
            HOW THE EMPIRE CONNECTS.
          </h2>
          <p className="text-base sm:text-lg text-[#DBDEE1]">
            A unified operating framework mapping internal innovation, external opportunities, milestone delivery, and verifiable proof.
          </p>
        </div>

        {/* Diagram 1: Full System Topology */}
        <div className="bg-[#11131A] border border-[#232736] rounded-2xl p-6 sm:p-10 space-y-6">
          <div className="flex items-center justify-between pb-4 border-b border-[#232736]">
            <div className="flex items-center gap-2">
              <Layers className="w-5 h-5 text-[#FFB020]" />
              <h3 className="text-base sm:text-lg font-bold text-white font-mono uppercase">
                SYSTEM TOPOLOGY DIAGRAM
              </h3>
            </div>
            <span className="text-xs font-mono text-[#949BA4]">Structured Workflows</span>
          </div>

          {/* ASCII & Visual Node Graph */}
          <div className="bg-[#08090D] border border-[#232736] rounded-xl p-6 sm:p-8 font-mono text-xs overflow-x-auto text-[#DBDEE1]">
            <div className="min-w-[500px] flex flex-col items-center space-y-4">
              {/* Node 1: JEMP Member */}
              <div className="px-6 py-2.5 rounded-lg bg-[#FFB020] text-black font-black text-sm shadow-lg shadow-[#FFB020]/20">
                JEMP# MEMBER
              </div>
              <div className="text-[#949BA4]">│</div>

              {/* Fork 1: B# and HC# */}
              <div className="grid grid-cols-2 gap-12 w-full max-w-md">
                <div className="flex flex-col items-center">
                  <div className="text-[#949BA4] mb-1">┌───────────────</div>
                  <div className="px-4 py-2 rounded-lg bg-[#181B24] border border-[#FFB020]/40 text-[#FFB020] font-bold text-center w-full">
                    B# BRAINSTORM<br />
                    <span className="text-[10px] text-[#949BA4] font-normal">Internal Ideas</span>
                  </div>
                </div>

                <div className="flex flex-col items-center">
                  <div className="text-[#949BA4] mb-1">───────────────┐</div>
                  <div className="px-4 py-2 rounded-lg bg-[#181B24] border border-[#38BDF8]/40 text-[#38BDF8] font-bold text-center w-full">
                    HC# CLIENT WORK<br />
                    <span className="text-[10px] text-[#949BA4] font-normal">External Contracts</span>
                  </div>
                </div>
              </div>

              <div className="text-[#949BA4]">└───────────────┬───────────────┘</div>
              <div className="text-[#949BA4]">│</div>

              {/* Node 3: P# Ongoing Project */}
              <div className="px-6 py-2.5 rounded-lg bg-[#181B24] border border-[#10B981]/60 text-[#10B981] font-bold text-sm text-center">
                P# ONGOING PROJECT (SQUAD EXECUTION)
              </div>
              <div className="text-[#949BA4]">│</div>

              {/* Fork 2: Build & Finance */}
              <div className="grid grid-cols-2 gap-12 w-full max-w-md">
                <div className="flex flex-col items-center">
                  <div className="text-[#949BA4] mb-1">┌───────────────</div>
                  <div className="px-4 py-2 rounded-lg bg-[#11131A] border border-[#232736] text-white text-center w-full">
                    BUILD<br />
                    <span className="text-[10px] text-[#949BA4]">Code, Milestones, UI</span>
                  </div>
                </div>

                <div className="flex flex-col items-center">
                  <div className="text-[#949BA4] mb-1">───────────────┐</div>
                  <div className="px-4 py-2 rounded-lg bg-[#11131A] border border-[#232736] text-white text-center w-full">
                    FINANCE (PAY#)<br />
                    <span className="text-[10px] text-[#949BA4]">95% Squad Payout</span>
                  </div>
                </div>
              </div>

              <div className="text-[#949BA4]">└───────────────┬───────────────┘</div>
              <div className="text-[#949BA4]">│</div>

              {/* Final Node: Timeline */}
              <div className="px-6 py-3 rounded-lg bg-gradient-to-r from-[#10B981] to-[#059669] text-black font-black text-sm shadow-lg shadow-[#10B981]/20 text-center">
                VERIFIABLE PROOF-OF-WORK TIMELINE
              </div>
            </div>
          </div>
        </div>

        {/* Realistic Demo Journey Walkthroughs */}
        <div className="space-y-4">
          <div className="flex items-center justify-between">
            <h3 className="text-base font-bold text-white font-mono uppercase">
              EXAMPLE JOURNEY WALKTHROUGHS (DEMO DATA)
            </h3>
            <span className="text-[11px] font-mono text-[#FFB020]">Illustrative Examples</span>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            {/* Walkthrough 1: Brainstorm to Project */}
            <div className="bg-[#11131A] border border-[#232736] rounded-xl p-5 space-y-3 text-xs">
              <div className="flex items-center justify-between pb-2 border-b border-[#232736]">
                <span className="font-bold text-[#FFB020] font-mono">DEMO 1: BRAINSTORM JOURNEY</span>
                <span className="font-mono text-[#949BA4]">B#027 → P#014</span>
              </div>
              <div className="space-y-2 font-mono text-[#DBDEE1]">
                <div className="p-2 rounded bg-[#181B24] border border-[#232736]">
                  <strong>1. Sept 2:</strong> JEMP#027 publishes B#027 (AI Resume Analyzer).
                </div>
                <div className="p-2 rounded bg-[#181B24] border border-[#232736]">
                  <strong>2. Sept 4:</strong> JEMP#014 & JEMP#008 apply and get accepted into squad.
                </div>
                <div className="p-2 rounded bg-[#181B24] border border-[#232736]">
                  <strong>3. Sept 5:</strong> Squad reaches consensus; promotes B#027 to active project P#014.
                </div>
                <div className="p-2 rounded bg-[#181B24] border border-[#10B981]/30 text-[#10B981]">
                  <strong>4. Sept 12:</strong> First AST milestone completed & attested to timelines.
                </div>
              </div>
            </div>

            {/* Walkthrough 2: Client Project to Payout */}
            <div className="bg-[#11131A] border border-[#232736] rounded-xl p-5 space-y-3 text-xs">
              <div className="flex items-center justify-between pb-2 border-b border-[#232736]">
                <span className="font-bold text-[#38BDF8] font-mono">DEMO 2: CLIENT PROJECT JOURNEY</span>
                <span className="font-mono text-[#949BA4]">HC#003 → PAY#001</span>
              </div>
              <div className="space-y-2 font-mono text-[#DBDEE1]">
                <div className="p-2 rounded bg-[#181B24] border border-[#232736]">
                  <strong>1. Aug 24:</strong> HC#003 reviewed & approved; 3-person squad selected.
                </div>
                <div className="p-2 rounded bg-[#181B24] border border-[#232736]">
                  <strong>2. Sept 12:</strong> Milestone 1 accepted by client; ₹100,000 payment received.
                </div>
                <div className="p-2 rounded bg-[#181B24] border border-[#232736]">
                  <strong>3. Sept 14:</strong> Proof verified by squad; 95% distribution proposal approved.
                </div>
                <div className="p-2 rounded bg-[#181B24] border border-[#10B981]/30 text-[#10B981]">
                  <strong>4. Sept 20:</strong> Payroll disbursed (PAY#001); contributors confirm receipt.
                </div>
              </div>
            </div>
          </div>
        </div>

      </div>
    </section>
  );
};
