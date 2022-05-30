import {
    CoverLetterStore,
    DocumentStore,
    ResumeListStore,
    UserStore
} from "./Store";

export default class RootStore {
    userStore: UserStore
    documentStore: DocumentStore
    resumeStore: ResumeListStore
    coverLetterStore: CoverLetterStore

    constructor() {
        // Wait for the window to be available before instantiating the stores
        if (typeof window !== 'undefined') {
            this.userStore = new UserStore();
            this.documentStore = new DocumentStore();
            this.resumeStore =  new ResumeListStore();
            this.coverLetterStore = new CoverLetterStore();
        }
    }
}