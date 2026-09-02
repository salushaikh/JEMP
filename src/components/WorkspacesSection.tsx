import React, { useState } from 'react';
import { Lightbulb, Rocket, Briefcase, ArrowRight, CheckCircle2, ChevronRight } from 'lucide-react';
import { DEMO_BRAINSTORMS, DEMO_PROJECTS } from '../data/joblessData';

export const WorkspacesSection: React.FC = () => {
  const [activeTab, setActiveTab] = useState<'BRAINSTORMS' | 'PROJECTS' | 'FLOW'>('BRAINSTORMS');

  const brainstormLifeCycle = [
    { label: 'IDEA', desc: 'Concept submitted' },
    { label: 'BRAINSTORMING', desc: 'Community feedback' },
    { label: 'COOKING', desc: 'Squad forming' },
    { label: 'PAUSED / CANCELLED', desc: 'Paused or archived' },
    { label: "IT'S ALIVE", desc: 'Prototype validated' },
    { label: 'PROJECT', desc: 'Promoted to P#' }
  ];

  const projectLifeCycle = [
    { label: 'ORIGIN', desc: 'B# or HC# source' },
    { label: 'PROJECT CREATED', desc: 'P# ID assigned' },
    { label: 'TEAM FORMED', desc: 'Agile squad confirmed' },
    { label: 'BUILD', desc: 'Active execution' },
    { label: 'MILESTONES', desc: 'Deliverables verified' },
    { label: 'COMPLETION', desc: 'Proof logged' }
  ];

  return (
    <section id="workspaces-section" className="py-16 sm:py-24 border-b border-[#1A1E29] bg-[#0B0D13]">
      <div className="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8 space-y-16">
        
        {/* Section Heading */}
        <div className="text-center max-w-3xl mx-auto space-y-4">
          <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-[#181B24] border border-[#232736] text-xs font-mono text-[#FFB020]">
            <span>04 • THE THREE WORKSPACES</span>
          </div>
          <h2 className="text-3xl sm:text-5xl font-black text-white font-mono tracking-tight uppercase">
            WHERE VALUE GETS BUILT.
          </h2>
          <p className="text-base sm:text-lg text-[#DBDEE1]">
            Jobless Empire is divided into three core operational workspaces, each with clear workflows and system identifiers.
          </p>
        </div>

        {/* The 3 Core Workspaces Cards */}
        <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
          {/* Workspace 01: B# */}
          <div className="bg-[#11131A] border border-[#FFB020]/30 rounded-2xl p-6 space-y-4 hover:border-[#FFB020]/60 transition-all flex flex-col justify-between">
            <div className="space-y-3">
              <div className="flex items-center justify-between">
                <div className="w-10 h-10 rounded-xl bg-[#FFB020]/10 border border-[#FFB020]/30 flex items-center justify-center text-[#FFB020]">
                  <Lightbulb className="w-5 h-5" />
                </div>
                <span className="font-mono text-xs font-black px-2.5 py-1 rounded bg-[#FFB020]/20 text-[#FFB020]">
                  B#
                </span>
              </div>
              <div>
                <span className="text-[10px] font-mono text-[#949BA4] uppercase">01 • THE LABORATORY</span>
                <h3 className="text-lg font-bold text-white">Brainstorming Club</h3>
              </div>
              <p className="text-xs sm:text-sm text-[#949BA4] leading-relaxed">
                For ideas, concepts, experiments, research, early-stage product ideas, startup concepts, and community initiatives.
              </p>
            </div>
            <div className="pt-3 border-t border-[#232736] text-[11px] font-mono text-[#DBDEE1]">
              Explore concepts & recruit squad
            </div>
          </div>

          {/* Workspace 02: P# */}
          <div className="bg-[#11131A] border border-[#10B981]/30 rounded-2xl p-6 space-y-4 hover:border-[#10B981]/60 transition-all flex flex-col justify-between">
            <div className="space-y-3">
              <div className="flex items-center justify-between">
                <div className="w-10 h-10 rounded-xl bg-[#10B981]/10 border border-[#10B981]/30 flex items-center justify-center text-[#10B981]">
                  <Rocket className="w-5 h-5" />
                </div>
                <span className="font-mono text-xs font-black px-2.5 py-1 rounded bg-[#10B981]/20 text-[#10B981]">
                  P#
                </span>
              </div>
              <div>
                <span className="text-[10px] font-mono text-[#949BA4] uppercase">02 • THE ENGINE ROOM</span>
                <h3 className="text-lg font-bold text-white">Ongoing Projects</h3>
              </div>
              <p className="text-xs sm:text-sm text-[#949BA4] leading-relaxed">
                For actual builds, organized collaboration, execution, sprint milestones, deliverables, and real project work.
              </p>
            </div>
            <div className="pt-3 border-t border-[#232736] text-[11px] font-mono text-[#DBDEE1]">
              Structured execution & milestone proof
            </div>
          </div>

          {/* Workspace 03: HC# */}
          <div className="bg-[#11131A] border border-[#38BDF8]/30 rounded-2xl p-6 space-y-4 hover:border-[#38BDF8]/60 transition-all flex flex-col justify-between">
            <div className="space-y-3">
              <div className="flex items-center justify-between">
                <div className="w-10 h-10 rounded-xl bg-[#38BDF8]/10 border border-[#38BDF8]/30 flex items-center justify-center text-[#38BDF8]">
                  <Briefcase className="w-5 h-5" />
                </div>
                <span className="font-mono text-xs font-black px-2.5 py-1 rounded bg-[#38BDF8]/20 text-[#38BDF8]">
                  HC#
                </span>
              </div>
              <div>
                <span className="text-[10px] font-mono text-[#949BA4] uppercase">03 • CLIENT OPPORTUNITIES</span>
                <h3 className="text-lg font-bold text-white">Client / Hiring</h3>
              </div>
              <p className="text-xs sm:text-sm text-[#949BA4] leading-relaxed">
                For external client opportunities, freelance-style projects, contract work, consulting, maintenance, and hiring opportunities.
              </p>
            </div>
            <div className="pt-3 border-t border-[#232736] text-[11px] font-mono text-[#DBDEE1]">
              Scoping, human selection & revenue share
            </div>
          </div>
        </div>

        {/* Visual Interconnection Diagram */}
        <div className="bg-[#11131A] border border-[#232736] rounded-2xl p-6 sm:p-8 space-y-6">
          <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-2 pb-4 border-b border-[#232736]">
            <div>
              <h3 className="text-lg font-bold text-white font-mono uppercase">
                HOW WORKSPACES INTERCONNECT
              </h3>
              <p className="text-xs text-[#949BA4]">
                Flexible origination pathways for internal experiments and client contracts.
              </p>
            </div>
            <span className="text-xs font-mono text-[#10B981] px-2.5 py-1 rounded bg-[#10B981]/10 border border-[#10B981]/30">
              Bi-directional Pipeline
            </span>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-3 gap-4 text-center">
            <div className="p-4 rounded-xl bg-[#181B24] border border-[#232736] space-y-1">
              <div className="text-xs font-mono font-bold text-[#FFB020]">B# (Internal Ideas)</div>
              <p className="text-xs text-[#949BA4]">Community experiments vetted by builder squads.</p>
              <div className="pt-2 text-xs font-mono text-white">↓ Promoted to P#</div>
            </div>

            <div className="p-4 rounded-xl bg-[#181B24] border border-[#10B981]/40 space-y-1">
              <div className="text-sm font-mono font-black text-[#10B981]">P# PROJECT (Execution)</div>
              <p className="text-xs text-[#DBDEE1]">Milestones, code commits, design assets, and delivery.</p>
              <div className="pt-2 text-xs font-mono text-[#10B981]">→ Immutable Timeline Proof</div>
            </div>

            <div className="p-4 rounded-xl bg-[#181B24] border border-[#232736] space-y-1">
              <div className="text-xs font-mono font-bold text-[#38BDF8]">HC# (External Client)</div>
              <p className="text-xs text-[#949BA4]">External contracts scoped with transparent terms.</p>
              <div className="pt-2 text-xs font-mono text-white">↓ Directly converted to P#</div>
            </div>
          </div>

          <div className="p-4 rounded-xl bg-[#141620] border border-[#232736] text-xs text-[#949BA4] space-y-1">
            <div><strong className="text-white">Important Operational Truths:</strong></div>
            <ul className="list-disc list-inside space-y-0.5 text-[#DBDEE1]">
              <li>Not every brainstorm (B#) becomes a project. Many remain ideas or get paused — that is completely normal.</li>
              <li>Not every project (P#) comes from a brainstorm; external client opportunities (HC#) can directly become projects.</li>
              <li>Every successful milestone creates documented proof on contributor private timelines.</li>
            </ul>
          </div>
        </div>

        {/* Tabbed Interactive Deep-Dive: Brainstorms vs Projects */}
        <div className="space-y-6">
          <div className="flex items-center justify-between border-b border-[#232736] pb-3">
            <div className="flex items-center gap-2">
              <button
                onClick={() => setActiveTab('BRAINSTORMS')}
                className={`px-4 py-2 rounded-lg text-xs font-mono font-bold transition-all ${
                  activeTab === 'BRAINSTORMS'
                    ? 'bg-[#FFB020] text-black shadow-md shadow-[#FFB020]/20'
                    : 'text-[#949BA4] hover:text-white bg-[#181B24]'
                }`}
              >
                B# Brainstorming Club
              </button>
              <button
                onClick={() => setActiveTab('PROJECTS')}
                className={`px-4 py-2 rounded-lg text-xs font-mono font-bold transition-all ${
                  activeTab === 'PROJECTS'
                    ? 'bg-[#10B981] text-black shadow-md shadow-[#10B981]/20'
                    : 'text-[#949BA4] hover:text-white bg-[#181B24]'
                }`}
              >
                P# Ongoing Projects
              </button>
            </div>
            <span className="text-xs font-mono text-[#949BA4] hidden sm:block">
              Illustrative Examples (Demo Data)
            </span>
          </div>

          {activeTab === 'BRAINSTORMS' && (
            <div className="space-y-6 animate-fadeIn">
              <div className="bg-[#11131A] border border-[#232736] rounded-2xl p-6 sm:p-8 space-y-6">
                <div>
                  <h3 className="text-xl font-bold text-white font-mono">
                    IDEAS ARE CHEAP. BUILDING IS THE PROOF.
                  </h3>
                  <p className="text-xs sm:text-sm text-[#949BA4] mt-1">
                    How ideas progress through the Brainstorming Club from raw proposal to squad execution.
                  </p>
                </div>

                {/* Brainstorm Lifecycle Bar */}
                <div className="space-y-2">
                  <div className="text-xs font-mono text-[#FFB020] font-bold">
                    BRAINSTORM LIFECYCLE:
                  </div>
                  <div className="grid grid-cols-2 sm:grid-cols-6 gap-2">
                    {brainstormLifeCycle.map((stage, idx) => (
                      <div key={idx} className="bg-[#181B24] border border-[#232736] p-2.5 rounded-lg text-center">
                        <div className="text-[11px] font-mono font-bold text-white">{stage.label}</div>
                        <div className="text-[9px] text-[#949BA4]">{stage.desc}</div>
                      </div>
                    ))}
                  </div>
                </div>

                {/* Demo Brainstorm Card */}
                <div className="bg-[#181B24] border border-[#FFB020]/30 rounded-xl p-5 space-y-4">
                  <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-2 pb-3 border-b border-[#232736]">
                    <div className="flex items-center gap-2">
                      <span className="px-2 py-0.5 rounded bg-[#FFB020]/20 text-[#FFB020] font-mono text-xs font-bold">
                        {DEMO_BRAINSTORMS[0].id}
                      </span>
                      <h4 className="text-sm sm:text-base font-bold text-white">
                        {DEMO_BRAINSTORMS[0].title}
                      </h4>
                    </div>
                    <span className="px-2 py-0.5 rounded bg-[#10B981]/20 text-[#10B981] font-mono text-xs font-bold self-start sm:self-auto">
                      STATUS: {DEMO_BRAINSTORMS[0].status}
                    </span>
                  </div>

                  <div className="grid grid-cols-1 md:grid-cols-2 gap-4 text-xs">
                    <div className="space-y-2">
                      <div>
                        <span className="text-[#949BA4] font-mono">PROBLEM:</span>
                        <p className="text-[#DBDEE1] mt-0.5">{DEMO_BRAINSTORMS[0].problem}</p>
                      </div>
                      <div>
                        <span className="text-[#949BA4] font-mono">PROPOSED DIRECTION:</span>
                        <p className="text-[#DBDEE1] mt-0.5">{DEMO_BRAINSTORMS[0].proposedDirection}</p>
                      </div>
                    </div>
                    <div className="space-y-2 bg-[#11131A] p-3 rounded-lg border border-[#232736]">
                      <div className="flex justify-between font-mono">
                        <span className="text-[#949BA4]">Target Deliverable:</span>
                        <span className="text-white font-bold">{DEMO_BRAINSTORMS[0].targetDeliverable}</span>
                      </div>
                      <div className="flex justify-between font-mono">
                        <span className="text-[#949BA4]">Commitment:</span>
                        <span className="text-white">{DEMO_BRAINSTORMS[0].expectedCommitment}</span>
                      </div>
                      <div className="flex justify-between font-mono">
                        <span className="text-[#949BA4]">Squad Members:</span>
                        <span className="text-[#10B981]">{DEMO_BRAINSTORMS[0].activeSquadMembers.join(', ')}</span>
                      </div>
                      <div className="pt-2 border-t border-[#232736]">
                        <span className="text-[10px] text-[#949BA4]">
                          Skills: {DEMO_BRAINSTORMS[0].desiredSkills.join(' • ')}
                        </span>
                      </div>
                    </div>
                  </div>
                </div>

              </div>
            </div>
          )}

          {activeTab === 'PROJECTS' && (
            <div className="space-y-6 animate-fadeIn">
              <div className="bg-[#11131A] border border-[#232736] rounded-2xl p-6 sm:p-8 space-y-6">
                <div>
                  <h3 className="text-xl font-bold text-white font-mono">
                    IDEAS BECOME REAL WHEN SOMEONE BUILDS THEM.
                  </h3>
                  <p className="text-xs sm:text-sm text-[#949BA4] mt-1">
                    Structured execution with milestones, PRDs, code commits, and documented outcomes.
                  </p>
                </div>

                {/* Project Lifecycle Bar */}
                <div className="space-y-2">
                  <div className="text-xs font-mono text-[#10B981] font-bold">
                    PROJECT EXECUTION LIFECYCLE:
                  </div>
                  <div className="grid grid-cols-2 sm:grid-cols-6 gap-2">
                    {projectLifeCycle.map((stage, idx) => (
                      <div key={idx} className="bg-[#181B24] border border-[#232736] p-2.5 rounded-lg text-center">
                        <div className="text-[11px] font-mono font-bold text-white">{stage.label}</div>
                        <div className="text-[9px] text-[#949BA4]">{stage.desc}</div>
                      </div>
                    ))}
                  </div>
                </div>

                {/* Demo Project Card */}
                <div className="bg-[#181B24] border border-[#10B981]/30 rounded-xl p-5 space-y-4">
                  <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-2 pb-3 border-b border-[#232736]">
                    <div className="flex items-center gap-2">
                      <span className="px-2 py-0.5 rounded bg-[#10B981]/20 text-[#10B981] font-mono text-xs font-bold">
                        {DEMO_PROJECTS[0].id}
                      </span>
                      <h4 className="text-sm sm:text-base font-bold text-white">
                        {DEMO_PROJECTS[0].title}
                      </h4>
                    </div>
                    <span className="text-xs font-mono text-[#FFB020] font-bold">
                      Progress: {DEMO_PROJECTS[0].progressPercentage}%
                    </span>
                  </div>

                  {/* Milestones Checklist */}
                  <div className="space-y-2">
                    <span className="text-[11px] font-mono text-[#949BA4] uppercase">
                      VERIFIED SPRINT MILESTONES:
                    </span>
                    <div className="space-y-2">
                      {DEMO_PROJECTS[0].milestones.map((m) => (
                        <div
                          key={m.id}
                          className="bg-[#11131A] p-3 rounded-lg border border-[#232736] flex items-center justify-between text-xs"
                        >
                          <div className="flex items-center gap-2">
                            <CheckCircle2
                              className={`w-4 h-4 ${m.completed ? 'text-[#10B981]' : 'text-[#949BA4]'}`}
                            />
                            <span className={m.completed ? 'text-white font-medium' : 'text-[#949BA4]'}>
                              {m.title}
                            </span>
                          </div>
                          {m.verification && (
                            <span className="font-mono text-[10px] text-[#10B981] bg-[#10B981]/10 px-2 py-0.5 rounded border border-[#10B981]/20">
                              {m.verification.verifiedBy}
                            </span>
                          )}
                        </div>
                      ))}
                    </div>
                  </div>
                </div>

              </div>
            </div>
          )}
        </div>

      </div>
    </section>
  );
};
