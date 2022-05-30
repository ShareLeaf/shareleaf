import "antd/dist/antd.css";
import { Suspense } from "react";
import { Styles } from "./styles/styles";
import Footer from "./components/Footer";
import Header from "./components/Header";
import i18n from "./translation";
import Home from "./pages/Home";

const LandingPage = () => (
        <Suspense fallback={null}>
            <Styles/>
            <Header />
            <Home/>
            <Footer />
        </Suspense>
);

export default LandingPage;
