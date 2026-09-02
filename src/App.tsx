import React, { useState, useRef, useEffect } from 'react';
import { NavSection } from './types';
import { JoblessTopBar } from './components/JoblessTopBar';
import { HeroSection } from './components/HeroSection';
import { CorePositioningSection } from './components/CorePositioningSection';
import { WorkspacesSection } from './components/WorkspacesSection';
import { ExternalWorkFlowSection } from './components/ExternalWorkFlowSection';
import { FinancialTransparencySection } from './components/FinancialTransparencySection';
import { ProofOfWorkTimelineSection } from './components/ProofOfWorkTimelineSection';
import { EcosystemDiagramsSection } from './components/EcosystemDiagramsSection';
import { MemberJourneySection } from './components/MemberJourneySection';
import { CommunityStandardsSection } from './components/CommunityStandardsSection';
import { FaqAccordionSection } from './components/FaqAccordionSection';
import { ApplicationFormCard } from './components/ApplicationFormCard';
import { FinalCtaFooter } from './components/FinalCtaFooter';

export default function App() {
  const [activeSection, setActiveSection] = useState<NavSection>(NavSection.HOW_IT_WORKS);

  // Section Refs for smooth scrolling
  const howItWorksRef = useRef<HTMLDivElement>(null);
  const workspacesRef = useRef<HTMLDivElement>(null);
  const proofOfWorkRef = useRef<HTMLDivElement>(null);
  const transparencyRef = useRef<HTMLDivElement>(null);
  const faqRef = useRef<HTMLDivElement>(null);
  const applyRef = useRef<HTMLDivElement>(null);

  const sectionRefs: Record<NavSection, React.RefObject<HTMLDivElement | null>> = {
    [NavSection.HOW_IT_WORKS]: howItWorksRef,
    [NavSection.WORKSPACES]: workspacesRef,
    [NavSection.PROOF_OF_WORK]: proofOfWorkRef,
    [NavSection.TRANSPARENCY]: transparencyRef,
    [NavSection.FAQ]: faqRef,
    [NavSection.APPLY]: applyRef,
    [NavSection.OVERVIEW]: howItWorksRef,
    [NavSection.PHILOSOPHY]: howItWorksRef,
    [NavSection.SAFETY]: howItWorksRef
  };

  const handleNavClick = (section: NavSection) => {
    setActiveSection(section);
    const ref = sectionRefs[section];
    if (ref && ref.current) {
      ref.current.scrollIntoView({ behavior: 'smooth', block: 'start' });
    }
  };

  // Scroll listener to update active section in header
  useEffect(() => {
    const handleScroll = () => {
      const scrollPosition = window.scrollY + 250;
      const navOrder: NavSection[] = [
        NavSection.HOW_IT_WORKS,
        NavSection.WORKSPACES,
        NavSection.TRANSPARENCY,
        NavSection.PROOF_OF_WORK,
        NavSection.FAQ,
        NavSection.APPLY
      ];

      for (const section of navOrder) {
        const ref = sectionRefs[section];
        if (ref?.current) {
          const { offsetTop, offsetHeight } = ref.current;
          if (scrollPosition >= offsetTop && scrollPosition < offsetTop + offsetHeight) {
            setActiveSection(section);
            break;
          }
        }
      }
    };

    window.addEventListener('scroll', handleScroll, { passive: true });
    return () => window.removeEventListener('scroll', handleScroll);
  }, []);

  return (
    <div className="min-h-screen bg-[#08090D] text-white font-sans selection:bg-[#FFB020] selection:text-black antialiased">
      {/* 1. Sticky Navigation */}
      <JoblessTopBar
        selectedSection={activeSection}
        onNavClick={handleNavClick}
      />

      {/* 2. Hero Section */}
      <div ref={howItWorksRef}>
        <HeroSection
          onApplyClick={() => handleNavClick(NavSection.APPLY)}
          onHowItWorksClick={() => {
            const el = document.getElementById('core-positioning-section');
            if (el) el.scrollIntoView({ behavior: 'smooth' });
          }}
        />

        {/* 3. Core Positioning, Problem & Philosophy */}
        <CorePositioningSection />
      </div>

      {/* 4. The Three Workspaces (B#, P#, HC#) */}
      <div ref={workspacesRef}>
        <WorkspacesSection />

        {/* 5. External Client Opportunities Flow & Scope Protection */}
        <ExternalWorkFlowSection />
      </div>

      {/* 6. Financial Transparency & 5% Fee */}
      <div ref={transparencyRef}>
        <FinancialTransparencySection />
      </div>

      {/* 7. Proof-of-Work Timeline */}
      <div ref={proofOfWorkRef}>
        <ProofOfWorkTimelineSection />
      </div>

      {/* 8. Connected Ecosystem Architecture & Demo Journeys */}
      <EcosystemDiagramsSection />

      {/* 9. Member Journey & Digital Office */}
      <MemberJourneySection />

      {/* 10. Community Standards, No-Guarantee Disclaimer & Who Should Join */}
      <CommunityStandardsSection />

      {/* 11. FAQ Accordion */}
      <div ref={faqRef}>
        <FaqAccordionSection />
      </div>

      {/* 12. Application Form Card */}
      <div ref={applyRef}>
        <ApplicationFormCard />
      </div>

      {/* 13. Final Closing Philosophy, CTAs & Footer */}
      <FinalCtaFooter onNavClick={handleNavClick} />
    </div>
  );
}
