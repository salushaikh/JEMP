import React, { useState } from 'react';
import { ChevronDown, Search, HelpCircle, ShieldCheck } from 'lucide-react';
import { FAQ_LIST } from '../data/joblessData';

export const FaqAccordionSection: React.FC = () => {
  const [openIds, setOpenIds] = useState<Record<string, boolean>>({ 'faq-1': true, 'faq-8': true, 'faq-10': true });
  const [searchQuery, setSearchQuery] = useState('');
  const [selectedCategory, setSelectedCategory] = useState<string>('ALL');

  const categories = [
    { id: 'ALL', label: 'All Questions' },
    { id: 'GENERAL', label: 'General & Identity' },
    { id: 'APPLICATION', label: 'Application & Review' },
    { id: 'BUILDING', label: 'Workspaces & Squads' },
    { id: 'PAYMENTS', label: 'Finances & 5% Fee' },
    { id: 'TIMELINE', label: 'Timeline & Privacy' }
  ];

  const toggleAccordion = (id: string) => {
    setOpenIds((prev) => ({ ...prev, [id]: !prev[id] }));
  };

  const filteredFaqs = FAQ_LIST.filter((item) => {
    const matchesCategory = selectedCategory === 'ALL' || item.category === selectedCategory;
    const matchesSearch =
      item.question.toLowerCase().includes(searchQuery.toLowerCase()) ||
      item.answer.toLowerCase().includes(searchQuery.toLowerCase());
    return matchesCategory && matchesSearch;
  });

  return (
    <section id="faq-section" className="py-16 sm:py-24 border-b border-[#1A1E29] bg-[#0E1017]">
      <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 space-y-12">
        
        {/* Header */}
        <div className="text-center space-y-4">
          <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-[#181B24] border border-[#232736] text-xs font-mono text-[#FFB020]">
            <HelpCircle className="w-3.5 h-3.5" />
            <span>12 • FREQUENTLY ASKED QUESTIONS</span>
          </div>
          <h2 className="text-3xl sm:text-5xl font-black text-white font-mono tracking-tight uppercase">
            CLEAR ANSWERS. ZERO FLUFF.
          </h2>
          <p className="text-sm sm:text-base text-[#DBDEE1]">
            Everything you need to know about membership, review criteria, workspaces, and financial transparency.
          </p>
        </div>

        {/* Search & Category Filter */}
        <div className="space-y-3">
          <div className="relative">
            <Search className="w-4 h-4 text-[#949BA4] absolute left-3.5 top-1/2 -translate-y-1/2" />
            <input
              type="text"
              placeholder="Search FAQ questions..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              className="w-full bg-[#11131A] border border-[#232736] rounded-xl pl-10 pr-4 py-3 text-sm text-white placeholder-[#949BA4] focus:outline-none focus:border-[#FFB020] transition-colors"
            />
          </div>

          <div className="flex items-center gap-2 overflow-x-auto pb-1">
            {categories.map((cat) => (
              <button
                key={cat.id}
                onClick={() => setSelectedCategory(cat.id)}
                className={`px-3 py-1.5 rounded-lg text-xs font-mono transition-all whitespace-nowrap ${
                  selectedCategory === cat.id
                    ? 'bg-[#FFB020] text-black font-bold shadow-sm'
                    : 'bg-[#11131A] text-[#DBDEE1] hover:bg-[#181B24] border border-[#232736]'
                }`}
              >
                {cat.label}
              </button>
            ))}
          </div>
        </div>

        {/* Accordion List */}
        <div className="space-y-3">
          {filteredFaqs.map((faq) => {
            const isOpen = !!openIds[faq.id];
            return (
              <div
                key={faq.id}
                className="bg-[#11131A] border border-[#232736] rounded-xl overflow-hidden transition-colors"
              >
                <button
                  onClick={() => toggleAccordion(faq.id)}
                  className="w-full p-4 sm:p-5 text-left flex items-center justify-between gap-3 hover:bg-[#181B24]/60 transition-colors"
                  aria-expanded={isOpen}
                >
                  <span className="text-sm sm:text-base font-bold text-white font-mono">
                    {faq.question}
                  </span>
                  <ChevronDown
                    className={`w-4 h-4 text-[#949BA4] shrink-0 transition-transform duration-200 ${
                      isOpen ? 'rotate-180 text-[#FFB020]' : ''
                    }`}
                  />
                </button>

                {isOpen && (
                  <div className="px-4 pb-5 sm:px-5 text-xs sm:text-sm text-[#DBDEE1] leading-relaxed border-t border-[#232736]/60 pt-3 bg-[#0D0F15]">
                    {faq.answer}
                  </div>
                )}
              </div>
            );
          })}

          {filteredFaqs.length === 0 && (
            <div className="text-center py-10 text-sm text-[#949BA4] bg-[#11131A] rounded-xl border border-[#232736]">
              No questions matched your search query.
            </div>
          )}
        </div>

      </div>
    </section>
  );
};
