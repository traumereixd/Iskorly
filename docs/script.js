// Iskorly Website JavaScript
// Handles smooth navigation, mobile menu, and fade-in animations

(function() {
    'use strict';
    
    // Mobile Menu Toggle
    const mobileMenuToggle = document.getElementById('mobileMenuToggle');
    const navLinks = document.getElementById('navLinks');
    
    if (mobileMenuToggle && navLinks) {
        mobileMenuToggle.addEventListener('click', function() {
            navLinks.classList.toggle('active');
            
            // Animate hamburger icon
            const spans = mobileMenuToggle.querySelectorAll('span');
            if (navLinks.classList.contains('active')) {
                spans[0].style.transform = 'rotate(45deg) translateY(7px)';
                spans[1].style.opacity = '0';
                spans[2].style.transform = 'rotate(-45deg) translateY(-7px)';
            } else {
                spans[0].style.transform = 'none';
                spans[1].style.opacity = '1';
                spans[2].style.transform = 'none';
            }
        });
        
        // Close mobile menu when clicking on a link
        const navLinkItems = navLinks.querySelectorAll('a');
        navLinkItems.forEach(link => {
            link.addEventListener('click', function() {
                if (window.innerWidth <= 768) {
                    navLinks.classList.remove('active');
                    const spans = mobileMenuToggle.querySelectorAll('span');
                    spans[0].style.transform = 'none';
                    spans[1].style.opacity = '1';
                    spans[2].style.transform = 'none';
                }
            });
        });
    }
    
    // Smooth scrolling for internal links
    document.querySelectorAll('a[href^="#"]').forEach(anchor => {
        anchor.addEventListener('click', function(e) {
            const href = this.getAttribute('href');
            if (href === '#') return;
            
            e.preventDefault();
            const target = document.querySelector(href);
            if (target) {
                const offsetTop = target.offsetTop - 60; // Account for fixed navbar
                window.scrollTo({
                    top: offsetTop,
                    behavior: 'smooth'
                });
            }
        });
    });
    
    // Navbar scroll effect
    const navbar = document.getElementById('navbar');
    let lastScrollTop = 0;
    
    window.addEventListener('scroll', function() {
        const scrollTop = window.pageYOffset || document.documentElement.scrollTop;
        
        if (scrollTop > 100) {
            navbar.style.boxShadow = '0 4px 6px rgba(0, 0, 0, 0.1)';
        } else {
            navbar.style.boxShadow = '0 1px 3px rgba(0, 0, 0, 0.1)';
        }
        
        lastScrollTop = scrollTop;
    });
    
    // Intersection Observer for fade-in animations
    const observerOptions = {
        root: null,
        rootMargin: '0px',
        threshold: 0.1
    };
    
    const observer = new IntersectionObserver(function(entries) {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                entry.target.classList.add('visible');
                // Optionally unobserve after animation
                // observer.unobserve(entry.target);
            }
        });
    }, observerOptions);
    
    // Observe all elements with fade-in class
    const fadeElements = document.querySelectorAll('.fade-in');
    fadeElements.forEach(el => {
        observer.observe(el);
    });
    
    // Analytics event stubs (no actual tracking)
    // These can be replaced with real analytics when needed
    window.trackEvent = function(category, action, label) {
        console.log('Analytics Event:', { category, action, label });
        // Example: Google Analytics
        // if (typeof gtag !== 'undefined') {
        //     gtag('event', action, {
        //         'event_category': category,
        //         'event_label': label
        //     });
        // }
    };
    
    // Track CTA button clicks
    document.querySelectorAll('.button, .cta-button').forEach(button => {
        button.addEventListener('click', function() {
            const text = this.textContent.trim();
            const href = this.getAttribute('href');
            trackEvent('CTA', 'click', `${text} - ${href}`);
        });
    });
    
    // Track section views (when section enters viewport)
    const sectionObserver = new IntersectionObserver(function(entries) {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                const sectionId = entry.target.id;
                trackEvent('Section', 'view', sectionId);
                sectionObserver.unobserve(entry.target); // Track only once
            }
        });
    }, { threshold: 0.5 });
    
    document.querySelectorAll('section[id]').forEach(section => {
        sectionObserver.observe(section);
    });
    
    // Handle reduced motion preference
    if (window.matchMedia('(prefers-reduced-motion: reduce)').matches) {
        // Disable animations for users who prefer reduced motion
        document.querySelectorAll('.fade-in').forEach(el => {
            el.classList.add('visible');
        });
    }
    
    // Email obfuscation helper (update email addresses as needed)
    // This is a simple example - replace with actual contact emails
    document.addEventListener('DOMContentLoaded', function() {
        // Example: Update mailto links to prevent spam
        // This can be enhanced with actual contact emails
        const mailtoLinks = document.querySelectorAll('a[href*="mailto:"]');
        mailtoLinks.forEach(link => {
            // Email addresses are already in the HTML for simplicity
            // In production, you might want to obfuscate them
            console.log('Mailto link found:', link.href);
        });
    });
    
    // Accessibility: Keyboard navigation for mobile menu
    if (mobileMenuToggle) {
        mobileMenuToggle.addEventListener('keydown', function(e) {
            if (e.key === 'Enter' || e.key === ' ') {
                e.preventDefault();
                this.click();
            }
        });
    }
    
    // Focus management for mobile menu
    document.addEventListener('keydown', function(e) {
        if (e.key === 'Escape' && navLinks && navLinks.classList.contains('active')) {
            navLinks.classList.remove('active');
            if (mobileMenuToggle) {
                const spans = mobileMenuToggle.querySelectorAll('span');
                spans[0].style.transform = 'none';
                spans[1].style.opacity = '1';
                spans[2].style.transform = 'none';
                mobileMenuToggle.focus();
            }
        }
    });
    
    // Page load performance tracking (stub)
    window.addEventListener('load', function() {
        const loadTime = performance.timing.loadEventEnd - performance.timing.navigationStart;
        trackEvent('Performance', 'page_load', `${loadTime}ms`);
    });
    
    console.log('Iskorly website initialized');
})();
