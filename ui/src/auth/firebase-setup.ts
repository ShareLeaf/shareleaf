import { initializeApp } from 'firebase/app';
import { getAuth } from 'firebase/auth';

const devConfig = {
    apiKey: "AIzaSyBbJAk_jCI_MNeFTy6OfDAVD9Xd3DA2FPE",
    authDomain: "rr-dev-eec3d.firebaseapp.com",
    projectId: "rr-dev-eec3d",
    storageBucket: "rr-dev-eec3d.appspot.com",
    messagingSenderId: "275142852688",
    appId: "1:275142852688:web:22dfef9e9265834db9d155",
    measurementId: "G-3CXH2EQ9EL"
};

const prodConfig = {};

// Initialize Firebase
const app = initializeApp(process.env.NODE_ENV === "production" ? prodConfig : devConfig);
export const auth = getAuth(app);