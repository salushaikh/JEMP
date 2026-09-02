import React, { useState } from 'react';
import { ShieldCheck, Receipt, DollarSign, Heart, CheckCircle, AlertCircle, ArrowRight, Layers } from 'lucide-react';
import { DEMO_FINANCIAL_STAGES } from '../data/joblessData';

export const FinancialTransparencySection: React.FC = () => {
  const [budgetUsd, setBudgetUsd] = useState<number>(1200); // ~$1,200 (₹100,000 INR)
  const [useFutureCharityModel, setUseFutureCharityModel] = useState<boolean>(false);
  const [selectedStageId, setSelectedStageId] = useState<string>('PAY#001');

  const safeBudget = Number.isFinite(budgetUsd) ? budgetUsd : 0;

  // Split calculations
  const adminPercentage = useFutureCharityModel ? 4 : 5;
  const charityPercentage = useFutureCharityModel ? 1 : 0;
  const contributorPercentage = 95;

  const adminAmount = (safeBudget * adminPercentage) / 100;
  const charityAmount = (safeBudget * charityPercentage) / 100;
  const contributorAmount = (safeBudget * contributorPercentage) / 100;

  const roles = [
    { name: 'Lead Architect / Systems', share: 40, color: '#FFB020' },
    { name: 'Full-Stack / Backend', share: 35, color: '#38BDF8' },
    { name: 'UI/UX & Frontend', share: 15, color: '#A855F7' },
    { name: 'QA & Documentation', share: 10, color: '#10B981' }
  ];

  const financialWorkflowSteps = [
    'CLIENT PAYMENT',
    'PAYMENT PROOF',
    'TEAM VERIFICATION',
    'DISTRIBUTION PROPOSAL',
    'TEAM APPROVAL',
    'TRANSFER TO ADMIN',
    'PAYROLL',
    'PAYMENT CONFIRMATION'
  ];

  const activeStage = DEMO_FINANCIAL_STAGES.find((s) => s.stageId === selectedStageId) || DEMO_FINANCIAL_STAGES[0];

  return (
    <section id="financial-transparency-section" className="py-16 sm:py-24 border-b border-[#1A1E29] bg-[#0B0D13]">
      <div className="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8 space-y-16">
        
        {/* Section Header */}
        <div className="text-center max-w-3xl mx-auto space-y-4">
          <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-[#181B24] border border-[#232736] text-xs font-mono text-[#10B981]">
            <span>06 • FINANCIAL TRANSPARENCY</span>
          </div>
          <h2 className="text-3xl sm:text-5xl font-black text-white font-mono tracking-tight uppercase">
            MONEY SHOULDN’T DISAPPEAR INTO A BLACK BOX.
          </h2>
          <p className="text-base sm:text-lg text-[#DBDEE1]">
            When an external client project generates revenue, Jobless Empire creates a transparent, auditable financial trail from invoice payment to contributor confirmation.
          </p>
        </div>

        {/* 1. Full Financial Workflow Flowbar */}
        <div className="bg-[#11131A] border border-[#232736] rounded-2xl p-6 sm:p-8 space-y-6">
          <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-2 pb-4 border-b border-[#232736]">
            <div>
              <h3 className="text-base sm:text-lg font-bold text-white font-mono uppercase">
                THE 8-STEP FINANCIAL TRANSPARENCY PIPELINE
              </h3>
              <p className="text-xs text-[#949BA4]">
                Strict separation between project funds, administrative fees, and contributor earnings.
              </p>
            </div>
            <span className="text-xs font-mono text-[#10B981] px-2.5 py-1 rounded bg-[#10B981]/10 border border-[#10B981]/30">
              Audit-Protected
            </span>
          </div>

          <div className="grid grid-cols-2 sm:grid-cols-4 lg:grid-cols-8 gap-2">
            {financialWorkflowSteps.map((step, idx) => (
              <div
                key={step}
                className="bg-[#181B24] border border-[#232736] rounded-lg p-2.5 text-center flex flex-col justify-center items-center"
              >
                <div className="text-[10px] font-mono text-[#10B981] font-bold">0{idx + 1}</div>
                <div className="text-[10px] font-mono font-bold text-white leading-tight mt-1">
                  {step}
                </div>
              </div>
            ))}
          </div>

          {/* Key Principles Callouts */}
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4 text-xs">
            <div className="p-4 rounded-xl bg-[#181B24] border border-[#232736] space-y-1.5">
              <div className="font-bold text-white font-mono flex items-center gap-1.5 text-sm">
                <CheckCircle className="w-4 h-4 text-[#10B981]" />
                <span>HUMAN PROOF REVIEW</span>
              </div>
              <p className="text-[#949BA4]">
                The bot manages the verification process; human team members and administrators evaluate actual receipts, bank UTR references, and transaction proofs. Reviewers can mark evidence as <strong>Approve</strong> or <strong>Not Satisfied</strong>.
              </p>
            </div>

            <div className="p-4 rounded-xl bg-[#181B24] border border-[#232736] space-y-1.5">
              <div className="font-bold text-white font-mono flex items-center gap-1.5 text-sm">
                <ShieldCheck className="w-4 h-4 text-[#FFB020]" />
                <span>FAIRNESS REVIEW & MATH VERIFICATION</span>
              </div>
              <p className="text-[#949BA4]">
                The bot verifies the mathematics; the team reviews the fairness. If even one required contributor objects to the proposed allocation, the proposal must be reviewed and revised.
              </p>
            </div>
          </div>
        </div>

        {/* 2. Interactive Revenue & Escrow Simulator */}
        <div className="bg-[#11131A] border border-[#232736] rounded-2xl p-6 sm:p-8 space-y-6">
          <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-3 pb-4 border-b border-[#232736]">
            <div>
              <div className="flex items-center gap-2">
                <DollarSign className="w-5 h-5 text-[#FFB020]" />
                <h3 className="text-lg font-bold text-white font-mono uppercase">
                  REVENUE SPLIT SIMULATOR
                </h3>
              </div>
              <p className="text-xs text-[#949BA4]">
                Standard model: 5% Admin fee + 95% Contributor pool.
              </p>
            </div>

            {/* Model Switcher */}
            <div className="flex items-center gap-2 bg-[#181B24] p-1 rounded-lg border border-[#232736]">
              <button
                onClick={() => setUseFutureCharityModel(false)}
                className={`px-3 py-1 rounded text-xs font-mono font-bold transition-all ${
                  !useFutureCharityModel ? 'bg-[#FFB020] text-black shadow-sm' : 'text-[#949BA4] hover:text-white'
                }`}
              >
                Current: 5% Admin Model
              </button>
              <button
                onClick={() => setUseFutureCharityModel(true)}
                className={`px-3 py-1 rounded text-xs font-mono font-bold transition-all flex items-center gap-1 ${
                  useFutureCharityModel ? 'bg-[#10B981] text-black shadow-sm' : 'text-[#949BA4] hover:text-white'
                }`}
              >
                <Heart className="w-3 h-3" />
                <span>Planned: 4% + 1% Charity</span>
              </button>
            </div>
          </div>

          {/* Interactive Range Slider */}
          <div className="space-y-3 bg-[#181B24] p-5 rounded-xl border border-[#232736]">
            <div className="flex items-center justify-between">
              <label className="text-xs font-mono text-[#949BA4] uppercase">
                Contract Total Value (USD / INR Approx):
              </label>
              <div className="text-2xl font-black font-mono text-[#10B981]">
                ${safeBudget.toLocaleString()} USD <span className="text-sm font-normal text-[#949BA4]">(~₹{(safeBudget * 83.5).toLocaleString()} INR)</span>
              </div>
            </div>

            <input
              type="range"
              min="500"
              max="10000"
              step="250"
              value={safeBudget}
              onChange={(e) => setBudgetUsd(Number(e.target.value))}
              className="w-full accent-[#FFB020] cursor-pointer"
            />

            <div className="flex items-center gap-2 overflow-x-auto pb-1 text-xs font-mono">
              <span className="text-[#949BA4] text-[10px]">PRESETS:</span>
              {[600, 1200, 2500, 5000].map((val) => (
                <button
                  key={val}
                  onClick={() => setBudgetUsd(val)}
                  className={`px-2.5 py-1 rounded ${
                    safeBudget === val ? 'bg-[#FFB020] text-black font-bold' : 'bg-[#11131A] text-[#DBDEE1] border border-[#232736]'
                  }`}
                >
                  ${val.toLocaleString()}
                </button>
              ))}
            </div>
          </div>

          {/* Allocation Breakdown */}
          <div className="space-y-2">
            <div className="flex items-center justify-between text-xs font-mono">
              <span className="text-[#10B981] font-bold">
                95% Contributor Pool (${contributorAmount.toLocaleString()} USD / ~₹{(contributorAmount * 83.5).toLocaleString()} INR)
              </span>
              <span className="text-[#FFB020] font-bold">
                {adminPercentage}% Ops (${adminAmount.toLocaleString()} USD)
                {useFutureCharityModel && ` • 1% Planned Charity ($${charityAmount.toLocaleString()} USD)`}
              </span>
            </div>

            <div className="w-full h-3.5 rounded-lg bg-[#11131A] border border-[#232736] overflow-hidden flex">
              <div
                className="h-full bg-gradient-to-r from-[#10B981] to-[#059669] transition-all duration-300"
                style={{ width: `${contributorPercentage}%` }}
              />
              <div
                className="h-full bg-[#FFB020] transition-all duration-300"
                style={{ width: `${adminPercentage}%` }}
              />
              {useFutureCharityModel && (
                <div
                  className="h-full bg-[#EC4899] transition-all duration-300"
                  style={{ width: `${charityPercentage}%` }}
                />
              )}
            </div>
          </div>

          {/* Squad Share Grid */}
          <div className="space-y-3 pt-2">
            <span className="text-xs font-mono font-bold text-[#949BA4] uppercase">
              EXAMPLE SQUAD ALLOCATION BREAKDOWN:
            </span>
            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-3">
              {roles.map((role, idx) => {
                const rolePayout = (contributorAmount * role.share) / 100;
                return (
                  <div key={idx} className="bg-[#181B24] border border-[#232736] p-4 rounded-xl space-y-1">
                    <div className="flex items-center justify-between text-xs">
                      <span className="font-semibold text-white truncate">{role.name}</span>
                      <span className="font-mono font-bold text-[10px] px-1.5 py-0.5 rounded bg-white/10 text-white">
                        {role.share}%
                      </span>
                    </div>
                    <div className="text-lg font-black font-mono text-[#10B981]">
                      ${rolePayout.toLocaleString(undefined, { maximumFractionDigits: 2 })} USD
                    </div>
                    <div className="text-[10px] text-[#949BA4] font-mono">
                      ~₹{(rolePayout * 83.5).toLocaleString(undefined, { maximumFractionDigits: 0 })} INR
                    </div>
                  </div>
                );
              })}
            </div>
          </div>
        </div>

        {/* 3. Verifiable Transparent Payroll Ledger (PAY#) */}
        <div className="bg-[#11131A] border border-[#232736] rounded-2xl p-6 sm:p-8 space-y-6">
          <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-3 pb-4 border-b border-[#232736]">
            <div className="flex items-center gap-2">
              <Receipt className="w-5 h-5 text-[#10B981]" />
              <div>
                <h3 className="text-lg font-bold text-white font-mono uppercase">
                  VERIFIED PAYROLL ESCROW LEDGER (PAY#)
                </h3>
                <p className="text-xs text-[#949BA4]">
                  Illustrative demo records showing public accountability on completed client milestones.
                </p>
              </div>
            </div>

            <div className="flex items-center gap-2">
              {DEMO_FINANCIAL_STAGES.map((st) => (
                <button
                  key={st.stageId}
                  onClick={() => setSelectedStageId(st.stageId)}
                  className={`px-3 py-1.5 rounded-lg text-xs font-mono font-bold transition-all ${
                    selectedStageId === st.stageId
                      ? 'bg-[#10B981] text-black shadow-md shadow-[#10B981]/20'
                      : 'bg-[#181B24] text-[#DBDEE1] border border-[#232736]'
                  }`}
                >
                  {st.stageId} (${st.totalBudgetUsd.toLocaleString()})
                </button>
              ))}
            </div>
          </div>

          {/* Active Ledger Detail */}
          <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
            <div className="bg-[#181B24] p-4 rounded-xl border border-[#232736] space-y-1">
              <span className="text-[10px] font-mono text-[#949BA4]">STAGE AMOUNT</span>
              <div className="text-xl font-bold font-mono text-white">
                ${activeStage.totalBudgetUsd.toLocaleString()} USD
              </div>
              <div className="text-xs text-[#949BA4]">{activeStage.projectTitle}</div>
            </div>

            <div className="bg-[#181B24] p-4 rounded-xl border border-[#232736] space-y-1">
              <span className="text-[10px] font-mono text-[#949BA4]">STAGE STATUS</span>
              <div>
                <span className="px-2 py-0.5 rounded text-xs font-mono font-bold bg-[#10B981]/20 text-[#10B981]">
                  {activeStage.status}
                </span>
              </div>
              <div className="text-xs text-[#949BA4]">Client: {activeStage.clientName}</div>
            </div>

            <div className="bg-[#181B24] p-4 rounded-xl border border-[#232736] space-y-1">
              <span className="text-[10px] font-mono text-[#949BA4]">CONTRIBUTOR POOL (95%)</span>
              <div className="text-xl font-bold font-mono text-[#10B981]">
                ${activeStage.contributorPoolUsd.toLocaleString()} USD
              </div>
              <div className="text-xs text-[#949BA4]">5% Admin Fee: ${activeStage.adminFeeUsd.toLocaleString()} USD</div>
            </div>
          </div>

          {/* Payout Distribution Table */}
          <div className="space-y-2">
            <span className="text-xs font-mono font-bold text-[#949BA4] uppercase">
              RECIPIENT DISBURSEMENT LEDGER:
            </span>
            <div className="overflow-x-auto rounded-xl border border-[#232736]">
              <table className="w-full text-left text-xs font-mono">
                <thead className="bg-[#181B24] text-[10px] text-[#949BA4] uppercase border-b border-[#232736]">
                  <tr>
                    <th className="p-3">Member</th>
                    <th className="p-3">Role</th>
                    <th className="p-3">Share</th>
                    <th className="p-3">Payout</th>
                    <th className="p-3">Status</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-[#232736] bg-[#11131A]">
                  {activeStage.payoutDistribution.map((pay, i) => (
                    <tr key={i} className="hover:bg-[#181B24]/50">
                      <td className="p-3 text-white font-bold">
                        <span className="text-[#10B981]">{pay.memberId}</span> — {pay.memberName}
                      </td>
                      <td className="p-3 text-[#949BA4] font-sans">{pay.role}</td>
                      <td className="p-3 text-white">{pay.percentage}%</td>
                      <td className="p-3 text-[#10B981] font-bold">${pay.amountUsd.toLocaleString()} USD</td>
                      <td className="p-3">
                        <span className="px-2 py-0.5 rounded bg-[#10B981]/20 text-[#10B981] text-[10px] font-bold">
                          {pay.status}
                        </span>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>
        </div>

      </div>
    </section>
  );
};
