// Smooth scrolling for navigation links
document.querySelectorAll('a[href^="#"]').forEach(anchor => {
    anchor.addEventListener('click', function (e) {
        e.preventDefault();
        const target = document.querySelector(this.getAttribute('href'));
        if (target) {
            target.scrollIntoView({
                behavior: 'smooth',
                block: 'start'
            });
        }
    });
});

// Navbar scroll effect
window.addEventListener('scroll', () => {
    const navbar = document.querySelector('.navbar');
    if (window.scrollY > 100) {
        navbar.style.background = 'rgba(255, 255, 255, 0.98)';
        navbar.style.boxShadow = '0 5px 20px rgba(0,0,0,0.1)';
    } else {
        navbar.style.background = 'rgba(255, 255, 255, 0.95)';
        navbar.style.boxShadow = 'none';
    }
});

// Intersection Observer for animations
const observerOptions = {
    threshold: 0.1,
    rootMargin: '0px 0px -50px 0px'
};

const observer = new IntersectionObserver((entries) => {
    entries.forEach(entry => {
        if (entry.isIntersecting) {
            entry.target.style.opacity = '1';
            entry.target.style.transform = 'translateY(0)';
        }
    });
}, observerOptions);

// Observe service cards
document.querySelectorAll('.service-card').forEach(card => {
    card.style.opacity = '0';
    card.style.transform = 'translateY(30px)';
    card.style.transition = 'all 0.6s ease';
    observer.observe(card);
});

// Mobile menu toggle
const hamburger = document.querySelector('.hamburger');
const navMenu = document.querySelector('.nav-menu');

hamburger.addEventListener('click', () => {
    hamburger.classList.toggle('active');
    navMenu.classList.toggle('active');
});

// Form submission handling
function showBookingForm() {
    alert('Booking system will be implemented soon! 🐾');
}

function scrollToFeatures() {
    document.querySelector('.features').scrollIntoView({
        behavior: 'smooth'
    });
}





// Add some interactive features
document.querySelectorAll('.service-card').forEach(card => {
    card.addEventListener('mouseenter', () => {
        card.style.transform = 'translateY(-10px) scale(1.02)';
    });

    card.addEventListener('mouseleave', () => {
        card.style.transform = 'translateY(0) scale(1)';
    });
});

// Loading animation
window.addEventListener('load', () => {
    document.body.style.opacity = '0';
    document.body.style.transition = 'opacity 0.5s ease';

    setTimeout(() => {
        document.body.style.opacity = '1';
    }, 100);
});

// Slideshow functionality with smooth transitions
let currentSlide = 0;
const slides = document.querySelectorAll('.slide');
const dots = document.querySelectorAll('.dot');
const indicators = document.querySelectorAll('.indicator');
let slideInterval;
let isTransitioning = false;

function showSlide(n) {
    if (isTransitioning) return;

    isTransitioning = true;

    // Remove active class from all slides
    slides.forEach(slide => {
        slide.classList.remove('active');
        slide.style.transition = 'all 1.2s ease'; // Slower transition
    });

    dots.forEach(dot => dot.classList.remove('active'));
    indicators.forEach(indicator => indicator.classList.remove('active'));

    // Calculate new slide index
    currentSlide = (n + slides.length) % slides.length;

    // Add active class to current slide with a small delay for smoothness
    setTimeout(() => {
        slides[currentSlide].classList.add('active');
        dots[currentSlide].classList.add('active');
        indicators[currentSlide].classList.add('active');

        // Reset the flag after transition completes
        setTimeout(() => {
            isTransitioning = false;
        }, 1200);
    }, 50);
}

function changeSlide(n) {
    showSlide(currentSlide + n);
    resetSlideInterval();
}

function goToSlide(n) {
    showSlide(n);
    resetSlideInterval();
}

function resetSlideInterval() {
    clearInterval(slideInterval);
    slideInterval = setInterval(() => {
        changeSlide(1);
    }, 6000); // Change slide every 6 seconds (increased from 5)
}

// Initialize slideshow
function initSlideshow() {
    // Add click events to dots
    dots.forEach((dot, index) => {
        dot.addEventListener('click', () => goToSlide(index));
    });

    // Add click events to indicators
    indicators.forEach((indicator, index) => {
        indicator.addEventListener('click', () => goToSlide(index));
    });

    // Start automatic slideshow
    resetSlideInterval();

    // Add keyboard navigation
    document.addEventListener('keydown', (e) => {
        if (e.key === 'ArrowLeft') changeSlide(-1);
        if (e.key === 'ArrowRight') changeSlide(1);
    });
}

// Add hover pause functionality
function addSlideHoverEffects() {
    const slideshow = document.querySelector('.hero-slideshow');

    slideshow.addEventListener('mouseenter', () => {
        clearInterval(slideInterval);
    });

    slideshow.addEventListener('mouseleave', () => {
        resetSlideInterval();
    });
}

// Add touch swipe support for mobile
function addTouchSupport() {
    const slideshow = document.querySelector('.slideshow-container');
    let startX = 0;
    let endX = 0;

    slideshow.addEventListener('touchstart', (e) => {
        startX = e.touches[0].clientX;
    });

    slideshow.addEventListener('touchend', (e) => {
        endX = e.changedTouches[0].clientX;
        handleSwipe();
    });

    function handleSwipe() {
        const swipeThreshold = 50;

        if (startX - endX > swipeThreshold) {
            // Swipe left - next slide
            changeSlide(1);
        } else if (endX - startX > swipeThreshold) {
            // Swipe right - previous slide
            changeSlide(-1);
        }
    }
}

// Initialize when page loads
document.addEventListener('DOMContentLoaded', function() {
    initSlideshow();
    addSlideHoverEffects();
    addTouchSupport();
});

console.log('🐕 Happy Paws Pet Care System Loaded Successfully!');
