import React, {FC, lazy, Suspense} from "react";

import ThemeProvider from './theme/ThemeProvider';
import { CssBaseline } from '@mui/material';

import {
    Routes,
    Route,
    Navigate, useLocation
} from "react-router-dom";
import BaseLayout from "./layouts/BaseLayout";
import SidebarLayout from 'src/layouts/SidebarLayout';
import SuspenseLoader from "./components/SuspenseLoader";
import {observer} from "mobx-react";
import {RRObjectType} from "./api";
import {useStore} from "./hooks/useStore";

const Loader = (Component) => (props) => (
    <Suspense fallback={<SuspenseLoader />}>
        <Component {...props} />
    </Suspense>
);

// Landing page
const LandingPage = Loader(lazy(() => import('src/content/pages/Landing')));

// Login
const Login = Loader(lazy(() => import('src/content/pages/Login')));

// Pages
const Overview = Loader(lazy(() => import('src/content/overview')));

// Dashboards

const Crypto = Loader(lazy(() => import('src/content/dashboards/Crypto')));


const Messenger = Loader(lazy(() => import('src/content/applications/Messenger')));
const CVDashboard = Loader(lazy(() => import('src/content/applications/CV/CVDashboard')));
const CVEditor = Loader(lazy(() => import('src/content/applications/CV/CVEditor')));
const UserProfile = Loader(lazy(() => import('src/content/applications/Users/profile')));
const UserSettings = Loader(lazy(() => import('src/content/applications/Users/settings')));

// Components

const Buttons = Loader(lazy(() => import('src/content/pages/Components/Buttons')));
const Modals = Loader(lazy(() => import('src/content/pages/Components/Modals')));
const Accordions = Loader(lazy(() => import('src/content/pages/Components/Accordions')));
const Tabs = Loader(lazy(() => import('src/content/pages/Components/Tabs')));
const Badges = Loader(lazy(() => import('src/content/pages/Components/Badges')));
const Tooltips = Loader(lazy(() => import('src/content/pages/Components/Tooltips')));
const Avatars = Loader(lazy(() => import('src/content/pages/Components/Avatars')));
const Cards = Loader(lazy(() => import('src/content/pages/Components/Cards')));
const Forms = Loader(lazy(() => import('src/content/pages/Components/Forms')));

// Status

const Status404 = Loader(lazy(() => import('src/content/pages/Status/Status404')));
const Status500 = Loader(lazy(() => import('src/content/pages/Status/Status500')));
const StatusComingSoon = Loader(lazy(() => import('src/content/pages/Status/ComingSoon')));
const StatusMaintenance = Loader(lazy(() => import('src/content/pages/Status/Maintenance')));

function RequireAuth({ user, children }) {
    const location = useLocation();
    return user ? (
        children
    ) : (
        <Navigate to="/login" replace state={{ path: location.pathname }} />
    );
}

function RequireAuthRedirect({ user, children }) {
    const location = useLocation();
    return user ? (
        <Navigate to="/cv/view/resumes" replace state={{ path: location.pathname }} />
    ) : (
        children
    );
}

const App: FC<any> = observer((props) => {
   const store = useStore();
   return (
        <ThemeProvider>
                <CssBaseline/>
                <Routes>
                    <Route path="/" element={<RequireAuthRedirect user={store.userStore.user}> <LandingPage/> </RequireAuthRedirect>}/>
                    <Route path="/login" element={<RequireAuthRedirect user={store.userStore.user}> <Login/> </RequireAuthRedirect>}/>
                    {/*<Route path="/login" element={<Login />}/>*/}
                    {/*<Route path="/overview" element={<Overview />}/>*/}
                    {/*<Route path="/status">*/}
                    {/*    <Route path="404" element={<Status404 />}/>*/}
                    {/*    <Route path="500" element={<Status500 />}/>*/}
                    {/*    <Route path="maintenance" element={<StatusMaintenance />}/>*/}
                    {/*    <Route path="coming-soon" element={<StatusComingSoon />}/>*/}
                    {/*</Route>*/}


                    {/*// </Route>*/}
                    <Route path="*" element={<Status404/>}/>

                    {/*<Route path="dashboard" element={<SidebarLayout />}>*/}
                    {/*    <Route path="/crypto" element={<Crypto />}/>*/}
                    {/*    <Route path="/messenger" element={<Messenger />}/>*/}
                    {/*</Route>*/}
                    <Route path="cv" element={<RequireAuth user={store.userStore.user}><SidebarLayout/></RequireAuth>}>
                        <Route path="/view/resumes" element={<CVDashboard key={1} cvType={RRObjectType.TYPE_R}/>}/>
                        <Route path="/view/cover-letters" element={<CVDashboard key={2} cvType={RRObjectType.TYPE_CL} />}/>
                        <Route path="*" element={<Status404/>}/>
                    </Route>
                    <Route path="document" element={<RequireAuth user={store.userStore.user}><BaseLayout/></RequireAuth>}>
                        <Route path="/diff" element={<CVDashboard/>}/>
                        <Route path="/:key" element={<CVEditor store={store}/>}/>
                        <Route path="*" element={<CVEditor store={store}/>}/>
                    </Route>

                    {/*<BaseLayout/>*/}
                    {/*<Route path="management" element={<SidebarLayout />}>*/}
                    {/*    <Route path="/profile/details" element={<UserProfile />}/>*/}
                    {/*    <Route path="/profile/settings" element={<UserSettings />}/>*/}
                    {/*</Route>*/}
                    {/*<Route path="components" element={<SidebarLayout />}>*/}
                    {/*    <Route path="/buttons" element={<Buttons />}/>*/}
                    {/*    <Route path="/modals" element={<Modals />}/>*/}
                    {/*    <Route path="/accordions" element={<Accordions />}/>*/}
                    {/*    <Route path="/tabs" element={<Tabs />}/>*/}
                    {/*    <Route path="/badges" element={<Badges />}/>*/}
                    {/*    <Route path="/tooltips" element={<Tooltips />}/>*/}
                    {/*    <Route path="/avatars" element={<Avatars />}/>*/}
                    {/*    <Route path="/cards" element={<Cards />}/>*/}
                    {/*    <Route path="/forms" element={<Forms />}/>*/}
                    {/*    <Route path="*" element={<Status404/>}/>*/}
                    {/*</Route>*/}
                </Routes>
        </ThemeProvider>
    )
});
export default App;
