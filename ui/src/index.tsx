import App from './App';
import ReactDOM from 'react-dom';
import 'src/utils/chart';
import './Editor.css'
import './App.css'
import * as serviceWorker from './serviceWorker';
import { HelmetProvider } from 'react-helmet-async';
import { BrowserRouter } from 'react-router-dom';
import 'nprogress/nprogress.css';
import { SidebarProvider } from './contexts/SidebarContext';
import {RootStoreProvider} from "./store/RootStoreContext";
import './register-fonts';

ReactDOM.render(
  <HelmetProvider>
    <SidebarProvider>
      <BrowserRouter>
          <RootStoreProvider>
            <App />
          </RootStoreProvider>
      </BrowserRouter>
    </SidebarProvider>
  </HelmetProvider>,
  document.getElementById('root')
);

serviceWorker.unregister();
