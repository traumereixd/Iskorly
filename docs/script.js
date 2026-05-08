// Iskorly Website JavaScript
// Handles smooth navigation, mobile menu, and fade-in animations

(function() {
    'use strict';
    
    /**
     * Load first available image from candidates list
     * Tries each image path in the data-candidates attribute until one successfully loads.
     * If all candidates fail, keeps the existing src attribute as fallback.
     * @param {HTMLImageElement} imgEl - Image element with data-candidates attribute
     */
    function loadFirstAvailable(imgEl) {
        const candidatesStr = imgEl.getAttribute('data-candidates');
        if (!candidatesStr) return;
        
        try {
            const candidates = JSON.parse(candidatesStr);
            if (!Array.isArray(candidates) || candidates.length === 0) return;
            
            let currentIndex = 0;
            
            function tryNext() {
                if (currentIndex >= candidates.length) {
                    // All candidates failed, keep existing src or fallback
                    return;
                }
                
                const path = candidates[currentIndex];
                const testImg = new Image();
                
                testImg.onload = function() {
                    // Success! Use this image
                    // Validate it's a safe relative path to prevent XSS
                    if (path && path.startsWith('assets/') && !path.includes('..') && !path.includes('://')) {
                        imgEl.src = path;
                    }
                    imgEl.removeAttribute('data-candidates'); // Clean up
                };
                
                testImg.onerror = function() {
                    // Try next candidate
                    currentIndex++;
                    tryNext();
                };
                
                // Only try safe paths for security: must start with 'assets/', no directory traversal, no protocol
                if (path && path.startsWith('assets/') && !path.includes('..') && !path.includes('://')) {
                    testImg.src = path;
                } else {
                    // Skip invalid paths
                    currentIndex++;
                    tryNext();
                }
            }
            
            tryNext();
        } catch (e) {
            // Silently fail and keep existing src
        }
    }
    
    // Mobile Menu Toggle
    const mobileMenuToggle = document.getElementById('mobileMenuToggle');
    const navLinks = document.getElementById('navLinks');
    
    if (mobileMenuToggle && navLinks) {
        mobileMenuToggle.addEventListener('click', function() {
            const isActive = navLinks.classList.toggle('active');
            
            // Update ARIA expanded state
            mobileMenuToggle.setAttribute('aria-expanded', isActive);
            
            // Animate hamburger icon
            const spans = mobileMenuToggle.querySelectorAll('span');
            if (isActive) {
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
                    mobileMenuToggle.setAttribute('aria-expanded', 'false');
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
        // Silent tracking - no console output
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
    
    // Load brand logo
    const brandLogo = document.querySelector('.logo');
    if (brandLogo && brandLogo.hasAttribute('data-candidates')) {
        loadFirstAvailable(brandLogo);
    }
    
    // Load footer logo
    const footerLogo = document.querySelector('.footer-logo');
    if (footerLogo && footerLogo.hasAttribute('data-candidates')) {
        loadFirstAvailable(footerLogo);
    }
    
    // Load member photos
    const memberPhotos = document.querySelectorAll('.member-photo');
    memberPhotos.forEach(function(img) {
        loadFirstAvailable(img);
    });
    
    // Email obfuscation helper (update email addresses as needed)
    // This is a simple example - replace with actual contact emails
    const mailtoLinks = document.querySelectorAll('a[href*="mailto:"]');
    mailtoLinks.forEach(link => {
        // Email addresses are already in the HTML for simplicity
        // In production, you might want to obfuscate them
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
                mobileMenuToggle.setAttribute('aria-expanded', 'false');
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

    // Formspree form submission handler
    const contactForm = document.getElementById('contactForm');
    const formAlert = document.getElementById('formAlert');

    if (contactForm && formAlert) {
        contactForm.addEventListener('submit', function(e) {
            e.preventDefault();

            const formspreeEndpoint = contactForm.getAttribute('data-formspree') || contactForm.getAttribute('action');

            if (!formspreeEndpoint || formspreeEndpoint.includes('your-form-id')) {
                formAlert.textContent = 'Form endpoint not configured properly.';
                formAlert.className = 'form-alert error';
                return;
            }

            // Get form data
            const formData = new FormData(contactForm);

            // Submit via fetch with JSON accept header for Formspree AJAX response
            fetch(formspreeEndpoint, {
                method: 'POST',
                body: formData,
                headers: {
                    'Accept': 'application/json'
                }
            })
            .then(response => {
                if (response.ok) {
                    formAlert.textContent = 'Thank you! Your message has been sent successfully.';
                    formAlert.className = 'form-alert success';
                    contactForm.reset();
                } else {
                    return response.json().then(data => {
                        throw new Error(data.error || 'Form submission failed');
                    }).catch(jsonError => {
                        // If JSON parsing fails, throw generic error
                        throw new Error('Form submission failed');
                    });
                }
            })
            .catch(error => {
                formAlert.textContent = 'Oops! There was a problem submitting your form. Please try again.';
                formAlert.className = 'form-alert error';
                console.error('Form submission error:', error);
            });
        });
    }
})();