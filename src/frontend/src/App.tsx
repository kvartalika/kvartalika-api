import { useState, useEffect } from 'react';
import Header from './components/Header';
import Footer from './components/Footer';
import HomePage from './pages/HomePage';
import ComplexPage from './pages/ComplexPage';
import ApartmentPage from './pages/ApartmentPage';
import PageLoader from './components/PageLoader';
import './App.css';

type PageType = 'home' | 'complex' | 'apartment';

function App() {
    const [currentPage, setCurrentPage] = useState<PageType>('home');
    const [isLoading, setIsLoading] = useState(false);
    const [isInitialLoad, setIsInitialLoad] = useState(true);
    const [isScrolled, setIsScrolled] = useState(false);

    useEffect(() => {
        // Симуляция начальной загрузки
        const timer = setTimeout(() => {
            setIsInitialLoad(false);
        }, 2000);

        return () => clearTimeout(timer);
    }, []);

    useEffect(() => {
        // Отслеживание прокрутки для изменения стиля header
        const handleScroll = () => {
            const scrollTop = window.scrollY;
            setIsScrolled(scrollTop > 100);
        };

        window.addEventListener('scroll', handleScroll);
        return () => window.removeEventListener('scroll', handleScroll);
    }, []);

    const navigateToPage = (page: PageType) => {
        if (page === currentPage) return;

        setIsLoading(true);

        // Симуляция загрузки
        setTimeout(() => {
            setCurrentPage(page);
            setIsLoading(false);

            // Плавная прокрутка наверх
            window.scrollTo({ top: 0, behavior: 'smooth' });
        }, 800);
    };

    const navigateToComplex = () => navigateToPage('complex');
    const navigateToApartment = () => navigateToPage('apartment');
    const navigateToHome = () => navigateToPage('home');

    if (isInitialLoad) {
        return <PageLoader />;
    }

    return (
        <div className="App">
            {isLoading && <PageLoader />}

            {/* Header теперь показывается на всех страницах */}
            <Header
                showOnHomePage={true}
                onNavigateToHome={navigateToHome}
                currentPage={currentPage}
                isScrolled={isScrolled}
            />

            <div className="page-transition">
                {currentPage === 'home' && (
                    <HomePage onNavigateToComplex={navigateToComplex} />
                )}
                {currentPage === 'complex' && (
                    <ComplexPage onNavigateToApartment={navigateToApartment} />
                )}
                {currentPage === 'apartment' && (
                    <ApartmentPage onNavigateBack={() => navigateToPage('complex')} />
                )}
            </div>

            <Footer />
        </div>
    );
}

export default App;