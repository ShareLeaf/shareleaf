import { useContext } from 'react';
import {RootStoreContext} from "../store/RootStoreContext";

export const useStore = () => useContext(RootStoreContext);
