import React, { useState, useEffect } from 'react';
import { NavSection } from '../types';
import { Menu, X, ArrowRight, ShieldCheck } from 'lucide-react';

interface JoblessTopBarProps {
  selectedSection: NavSection;
  onNavClick: (section: NavSection) => void;
}

export const JoblessTopBar: React.FC<JoblessTopBarProps> = ({
  selectedSection,
  onNavClick
}) => {
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);
  const [scrolled, setScrolled] = useState(false);

  useEffect(() => {
    const handleScroll = () => {
      setScrolled(window.scrollY > 20);
    };
    window.addEventListener('scroll', handleScroll);
    return () => window.removeEventListener('scroll', handleScroll);
  }, []);

  const navItems = [
    { label: 'How It Works', section: NavSection.HOW_IT_WORKS },
    { label: 'Workspaces', section: NavSection.WORKSPACES },
    { label: 'Proof of Work', section: NavSection.PROOF_OF_WORK },
    { label: 'Transparency', section: NavSection.TRANSPARENCY },
    { label: 'FAQ', section: NavSection.FAQ },
    { label: 'Apply', section: NavSection.APPLY }
  ];

  return (
    <header
      id="main-navigation-header"
      className={`sticky top-0 z-50 transition-all duration-300 ${
        scrolled
          ? 'bg-[#0B0D13]/90 backdrop-blur-md border-b border-[#232736]/80 shadow-lg'
          : 'bg-[#0B0D13] border-b border-[#1A1E29]'
      }`}
    >
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex items-center justify-between h-16 sm:h-18">
          {/* Logo & Tagline */}
          <button
            onClick={() => onNavClick(NavSection.HOW_IT_WORKS)}
            className="flex items-center gap-3 text-left group"
            id="brand-logo-btn"
          >
            <div className="w-8 h-8 rounded-lg bg-gradient-to-br from-[#FFB020] to-[#E69900] flex items-center justify-center text-black font-black font-mono text-sm tracking-tighter shadow-md group-hover:scale-105 transition-transform">
              JE
            </div>
            <div>
              <div className="text-white font-black tracking-wider text-sm sm:text-base flex items-center gap-1.5 font-mono">
                <span>JOBLESS EMPIRE</span>
                <span className="hidden sm:inline-block w-1.5 h-1.5 rounded-full bg-[#10B981]" />
              </div>
              <p className="text-[11px] text-[#949BA4] font-medium hidden sm:block">
                No job. Still building.
              </p>
            </div>
          </button>

          {/* Desktop Navigation */}
          <nav className="hidden md:flex items-center gap-1 lg:gap-2">
            {navItems.map((item) => {
              const isActive = selectedSection === item.section;
              const sectionSlug = (item.section || '').toLowerCase();
              return (
                <button
                  key={item.section}
                  id={`nav-link-${sectionSlug}`}
                  onClick={() => onNavClick(item.section)}
                  className={`px-3 py-1.5 rounded-lg text-xs font-medium transition-all ${
                    isActive
                      ? 'text-[#FFB020] bg-[#181B24] font-semibold border border-[#FFB020]/20'
                      : 'text-[#DBDEE1] hover:text-white hover:bg-[#181B24]/60'
                  }`}
                >
                  {item.label}
                </button>
              );
            })}
          </nav>

          {/* Actions */}
          <div className="flex items-center gap-3">
            <button
              id="header-apply-cta"
              onClick={() => onNavClick(NavSection.APPLY)}
              className="bg-[#FFB020] hover:bg-[#E69900] text-black font-bold text-xs sm:text-sm px-4 py-2 rounded-lg transition-all flex items-center gap-1.5 shadow-md shadow-[#FFB020]/15 active:scale-95"
            >
              <span>Apply to Join</span>
              <ArrowRight className="w-3.5 h-3.5" />
            </button>

            {/* Mobile menu button */}
            <button
              id="mobile-menu-toggle"
              onClick={() => setMobileMenuOpen(!mobileMenuOpen)}
              className="md:hidden p-2 rounded-lg text-[#949BA4] hover:text-white hover:bg-[#181B24] transition-colors"
              aria-label="Toggle navigation menu"
            >
              {mobileMenuOpen ? <X className="w-5 h-5" /> : <Menu className="w-5 h-5" />}
            </button>
          </div>
        </div>
      </div>

      {/* Mobile Drawer */}
      {mobileMenuOpen && (
        <div
          id="mobile-nav-drawer"
          className="md:hidden bg-[#11131A] border-b border-[#232736] px-4 py-4 space-y-2 animate-fadeIn"
        >
          {navItems.map((item) => {
            const sectionSlug = (item.section || '').toLowerCase();
            return (
              <button
                key={item.section}
                id={`mobile-nav-${sectionSlug}`}
                onClick={() => {
                  onNavClick(item.section);
                  setMobileMenuOpen(false);
                }}
                className={`w-full text-left px-3 py-2 rounded-lg text-sm font-medium transition-colors ${
                  selectedSection === item.section
                    ? 'text-[#FFB020] bg-[#181B24] font-bold'
                    : 'text-[#DBDEE1] hover:bg-[#181B24]'
                }`}
              >
                {item.label}
              </button>
            );
          })}
          <div className="pt-2 border-t border-[#232736] flex items-center justify-between text-xs text-[#949BA4]">
            <span className="flex items-center gap-1">
              <ShieldCheck className="w-3.5 h-3.5 text-[#10B981]" />
              <span>Private Builder Community</span>
            </span>
            <span className="font-mono text-[11px] text-[#FFB020]">Manual Review</span>
          </div>
        </div>
      )}
    </header>
  );
};
