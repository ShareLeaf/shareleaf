import {createContext, FC, ReactNode} from "react";

import PropTypes from "prop-types";
import {CoverLetterStore, DocumentStore, ResumeListStore, UserStore} from "./Store";
import RootStore from "./RootStore";

let store: RootStore

interface RootStoreContextValue {
    userStore: UserStore
    documentStore: DocumentStore
    resumeStore: ResumeListStore
    coverLetterStore: CoverLetterStore
}

interface RootStoreProviderProps {
    children: ReactNode;
}

export const RootStoreContext = createContext<RootStoreContextValue | undefined>(undefined);

export const RootStoreProvider: FC<RootStoreProviderProps> = (props) => {
    const { children } = props;
    const root = store ?? new RootStore()
    return (
        <RootStoreContext.Provider
            value={root}>
            {children}
        </RootStoreContext.Provider>
    );
}

RootStoreProvider.propTypes = {
    children: PropTypes.node.isRequired
};