# Upgrade Roadmap: Professional Job Portal SaaS Platform

This roadmap outlines the systematic upgrade of the "Job Portal Management System" into a premium, enterprise-grade SaaS platform.

## Phase 1: Foundation & Security Upgrade
- [ ] **Database Schema Expansion**: Add fields for company status, business details, profile scores, and notifications.
- [ ] **Role Overhaul**: Standardize roles to `SUPER_ADMIN`, `ADMIN`, `COMPANY`, and `JOB_SEEKER`.
- [ ] **OTP System Implementation**:
    - [ ] Registration OTP workflow.
    - [ ] Login 2FA (Optional/Configurable).
    - [ ] Forgot Password OTP flow.
    - [ ] Brute-force protection & Rate limiting.

## Phase 2: Company Onboarding & Admin Approval
- [ ] **Company Registration Flow**: Detailed form with GST, Business ID, and logo upload.
- [ ] **Approval Status Logic**: Implement `PENDING`, `APPROVED`, `REJECTED` states.
- [ ] **Admin Approval Dashboard**: Interface for admins to review, approve, or reject company applications.
- [ ] **Automated Email Notifications**: Beautiful HTML templates for approval/rejection alerts.

## Phase 3: Advanced Job Seeker Module
- [ ] **Profile Completion Engine**: Logic to calculate profile score (Photo + Resume + Education + Experience).
- [ ] **Resume Management**: Enhanced upload and tracking.
- [ ] **Application Tracking**: User-friendly timeline for job application status (Applied -> Interview -> Hired/Rejected).
- [ ] **Saved Jobs & Notifications**: Real-time alerts for new jobs and status changes.

## Phase 4: Enterprise Dashboards
- [ ] **Admin Dashboard**: Analytics overview, user management, and report generation (Export to PDF/Excel).
- [ ] **Company Dashboard**: Job management, applicant tracking, and interview scheduling.
- [ ] **Job Seeker Dashboard**: Recommended jobs based on skills and application overview.

## Phase 5: UI/UX & Theme Excellence
- [ ] **Glassmorphism Persistence**: Ensure theme preference is saved in the database for logged-in users.
- [ ] **Home Page Redesign**: High-conversion hero section, featured companies, and live stats.
- [ ] **Responsive Refinement**: Fix all layout misalignments and ensure perfect mobile compatibility.

## Phase 6: Final Integration & Security Audit
- [ ] **Security Hardening**: Session security, BCrypt verification, and CSRF protection.
- [ ] **Search & Filter Upgrade**: Advanced multi-parameter search for jobs and companies.
- [ ] **End-to-End Testing**: Validate all OTP flows and approval workflows.
