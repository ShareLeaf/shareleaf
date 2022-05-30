import {action, autorun, makeObservable, observable} from 'mobx';
import firebase from "firebase/compat/app";
import {RRCVMetadata, RRCVTemplate, RRResume} from "../api";


/**
 * Stores and persists the Firebase user object. This object cannot be persisted
 * properly. The getIdToken() and other functions are no longer available after
 * deserializing. So we must always use the auth object to get the idToken.
 *
 * <p>
 *     Use this user object for routing between protected and unprotected pages.
 * </p>
 */
export class UserStore {

     user: firebase.User  | null
     displayName: string = ""
     avatar: string = ""
     jobTitle: string = "Job Title Unknown"

    constructor() {
        makeObservable(this);
        const storedJson = localStorage.getItem(StoreKey.USER_STORE);
        if (storedJson) Object.assign(this, JSON.parse(storedJson));
    }

    toJSON() {
        const {user, displayName, avatar, jobTitle} = this
        return {
            user,
            displayName,
            avatar,
            jobTitle
        }
    }

    @action
    setUser(user: firebase.User) {
        this.user = user;
        if (user !== null) {
            this.displayName = user.displayName
            this.avatar = user.photoURL
        }
        localStorage.setItem(StoreKey.USER_STORE, JSON.stringify(this))
    }

    @action
    reset() {
        this.user = null;
        this.displayName = ""
        this.avatar = ""
        this.jobTitle = ""
        localStorage.removeItem(StoreKey.USER_STORE);
    }

    @action
    setJobTitle(jobTitle: string) {
        this.jobTitle = jobTitle;
        localStorage.setItem(StoreKey.USER_STORE, JSON.stringify(this))
    }
}

export class DocumentStore {
    objectKey: string | null
    content: RRResume  | null
    objectName: string | null
    templateType: RRCVTemplate | null

    constructor() {
        makeObservable(this);
        const storedJson = localStorage.getItem(StoreKey.DOCUMENT_STORE);
        if (storedJson) Object.assign(this, JSON.parse(storedJson));
        autorun(() => {
            localStorage.setItem(StoreKey.DOCUMENT_STORE, JSON.stringify(this))
        })
    }

    toJSON() {
        const {objectKey, content, objectName, templateType} = this
        return {
            objectKey,
            content,
            objectName,
            templateType
        }
    }

    @action
    setContent(content: RRResume) {
        if (content) {
            this.objectKey = content.cv_key
            this.content = content;
            this.objectName = content.cv_title
        } else {
            this.reset()
        }
    }

    @action
    setObjectName(objectName: string) {
        this.objectName = objectName
    }

    @action
    setTemplateType(templateType: RRCVTemplate) {
        this.templateType = templateType
    }

    @action
    reset() {
        this.objectKey = null;
        this.content = null;
        this.objectName = null;
        this.templateType = null;
        localStorage.removeItem(StoreKey.DOCUMENT_STORE);
    }
}

export class ResumeListStore {

     records: Array<RRCVMetadata> = new Array<RRCVMetadata>()
     objectKeys: Set<string> = new Set<string>()
     pageNum: number = PageDefaults.pageNum;
     pageSize: number = PageDefaults.pageSize;
     totalRecords: number = 0;

    constructor() {
        makeObservable(this);
        const storedJson = localStorage.getItem(StoreKey.RESUME_STORE);
        if (storedJson) Object.assign(this, JSON.parse(storedJson));
        autorun(() => {
            localStorage.setItem(StoreKey.RESUME_STORE, JSON.stringify(this))
        })
    }

    toJSON() {
        const {records, objectKeys, pageNum, pageSize, totalRecords} = this
        return {
            records,
            objectKeys,
            pageNum,
            pageSize,
            totalRecords
        }
    }

    @action
    setRecords(records: Array<RRCVMetadata>, append?: boolean) {
        if (append) {
            this.records = [...this.records, ...records];
        } else {
            this.records = records
        }
        if (records) {
            this.objectKeys = new Set<string>()
            for (let record of records) {
                this.objectKeys.add(record.cv_key)
            }
        }
    }

    @action
    setPageNum(pageNum: number) {
        this.pageNum = pageNum
    }

    @action
    setPageSize(pageSize: number) {
        this.pageSize = pageSize;
    }

    @action
    setTotalRecords(totalRecords: number) {
        this.totalRecords = totalRecords;
    }

    @action
    reset() {
        this.totalRecords = 0;
        this.records = new Array<RRCVMetadata>();
        this.objectKeys = null;
        this.pageSize = PageDefaults.pageSize;
        this.pageNum = PageDefaults.pageNum;
        localStorage.removeItem(StoreKey.RESUME_STORE);
    }
}

export class CoverLetterStore {

     records: Array<RRCVMetadata> = new Array<RRCVMetadata>()
     objectKeys: Set<string> = new Set<string>()
     pageNum: number = PageDefaults.pageNum;
     pageSize: number = PageDefaults.pageSize;
     totalRecords: number = 0;

    constructor() {
        makeObservable(this);
        const storedJson = localStorage.getItem(StoreKey.COVER_LETTER_STORE);
        if (storedJson) Object.assign(this, JSON.parse(storedJson));
        autorun(() => {
            localStorage.setItem(StoreKey.COVER_LETTER_STORE, JSON.stringify(this))
        })
    }

    toJSON() {
        const {records, objectKeys, pageNum, pageSize, totalRecords} = this
        return {
            records,
            objectKeys,
            pageNum,
            pageSize,
            totalRecords
        }
    }

    @action
    setRecords(records: Array<RRCVMetadata>) {
        this.records = records
        if (records) {
            this.objectKeys = new Set<string>()
            for (let record of records) {
                this.objectKeys.add(record.cv_key)
            }
        }
    }

    @action
    setPageNum(pageNum: number) {
        this.pageNum = pageNum
    }

    @action
    setPageSize(pageSize: number) {
        this.pageSize = pageSize
    }

    @action
    setTotalRecords(totalRecords: number) {
        this.totalRecords = totalRecords;
    }

    @action
    addRecord(record: RRCVMetadata) {
        if (record) {
            this.records = this.records.slice() // copy the existing list to trigger a re-render
            this.records.push(record)
            this.objectKeys.add(record.cv_key)
        }
    }

    @action
    reset() {
        this.records = new Array<RRCVMetadata>()
        this.objectKeys = null
        localStorage.removeItem(StoreKey.COVER_LETTER_STORE);
    }
}

export const StoreKey = {
    USER_STORE: "user",
    DOCUMENT_STORE: "document",
    RESUME_STORE: "resumes",
    COVER_LETTER_STORE: "cover_letters"
}

export const PageDefaults = {
    pageNum: 0,
    pageSize: 10
}