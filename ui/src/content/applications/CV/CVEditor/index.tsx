import {Helmet} from 'react-helmet-async';
import React, {FC, useEffect, useState} from 'react';
import {TemplateManager} from "./TemplateManager";
import {auth} from "../../../../auth/firebase-setup";
import {headerConfig} from "../../../../auth/headerConfig";
import SuspenseLoader from "../../../../components/SuspenseLoader";
import * as _ from "lodash";
import {CVApi, RRCVTemplate, RRResume} from "../../../../api";
import {Grid} from "@mui/material";
import MuiAlert from "@mui/material/Alert";
import ResumeFormEditor from "./ResumeFormEditor";
import RootStore from 'src/store/RootStore';
import {observer} from "mobx-react";

const serverThrottle = 60000;
const localStorageThrottle = 15000;

interface CVEditorProps {
    store: RootStore
}

const CVEditor: FC<CVEditorProps> = observer((props) => {
    const [reRenderPdf, setReRenderPdf] = useState<number>(0);
    const [documentName, setDocumentName] = useState<string>("");
    const [editorContent, setEditorContent] = useState<RRResume | undefined>(undefined);
    const [objectKey, setObjectKey] = useState<string>("");
    const [showProgressBar, setShowProgressBar] = useState<boolean>(false);
    const [displayIsSaving, setDisplayIsSaving] = useState<boolean>(false);
    const [editMode, setEditMode] = useState<boolean>(true);
    const [templateType, setTemplateType] = useState<RRCVTemplate | undefined>(undefined);
    const [cvNotFound, setCvNotFound] = useState<boolean>(false);

    const setContent = (): void => {
        setTemplateType(props.store.documentStore.templateType);
        setEditorContent(props.store.documentStore.content);
        setReRenderPdf(reRenderPdf + 1);
        setShowProgressBar(false);
    }

    const onEditToggle = (): void => {
        setEditMode(!editMode);
    }

    const onDocumentLoad = (): void => {
        setTemplateType(props.store.documentStore.templateType);
        setObjectKey(props.store.documentStore.objectKey);
        setDocumentName(props.store.documentStore.objectName);
    }

    const showSavingProgress = (): void => {
        setDisplayIsSaving(true);
    }

    const hideSavingProgress = (): void => {
        setDisplayIsSaving(false);
    }

    const onEditorChange = (_editorContent: RRResume): void => {
        setEditorContent({..._editorContent});
        throttledSaveToServer();
        throttledSaveToLocalStorage();
    }

    /**
     * Save to the server every 60 seconds
     */
    const throttledSaveToServer = _.throttle(() => {
        setTimeout(() => {
            saveDocument()
        }, serverThrottle);
    }, serverThrottle);

    // Save to local storage every 15 seconds
    const throttledSaveToLocalStorage = _.throttle(() => {
        setTimeout(() => {
            props.store.documentStore.setContent(editorContent);
        }, localStorageThrottle);
    }, localStorageThrottle);

    /**
     * Encode content to Base64 before saving
     *
     * @param decodedString content to encode to Base64
     */
    const encodeContent = (decodedString: string): string => {
        try {
            return window.btoa(unescape(encodeURIComponent( decodedString )));
        } catch (e)  {
            return decodedString
        }
    }

    /**
     * Save the document on the server
     */
    const saveDocument = (): void => {
        showSavingProgress();
        auth.onAuthStateChanged(user => {
            if (user) {
                user.getIdToken(false)
                    .then(token => {
                        const rrResume: RRResume = editorContent
                        new CVApi(headerConfig(token))
                            .updateCVData(rrResume)
                            .then((response) => {
                                props.store.documentStore.setContent(rrResume)
                                setReRenderPdf(reRenderPdf+1);
                                hideSavingProgress();
                            }).catch(e => console.log(e))
                    }).catch(e => console.log(e))
            }
        })
    }

    /**
     * Decode content from Base64 before displaying
     *
     * @param encodedString Base64-encoded content
     */
    const decodeContent = (encodedString: string): string => {
        try {
            return decodeURIComponent(escape(window.atob( encodedString )));
        } catch (e)  {
            return encodedString
        }
    }

    const fetchCVDataByKey = (key: string) => {
        auth.onAuthStateChanged(user => {
            if (user) {
                setShowProgressBar(true);
                user.getIdToken(false)
                    .then(token => {
                        setCvNotFound(false);
                        new CVApi(headerConfig(token))
                            .getCVData(key)
                            .then(result => {
                                if (result.data.resume) {
                                    props.store.documentStore.setContent(result.data.resume);
                                    props.store.documentStore.setTemplateType(result.data.template_type);
                                    setContent()
                                    onDocumentLoad()
                                } else {
                                    setCvNotFound(true);
                                    setShowProgressBar(false);
                                }

                            }).catch(e => {
                                console.log(e);
                                setShowProgressBar(false);
                                setCvNotFound(true);
                        })
                    }).catch(e => console.log(e))
            }
        })
    }

    useEffect(() => {
        const tokens =  window.location.pathname.split("/");
        const key = tokens[tokens.length-1]
        if (tokens.length < 2) {
            window.location.pathname = "/404"
        } else {
            fetchCVDataByKey(key)
        }

        // Reset the page num so the paginated view goes back to
        // the first page
        props.store.resumeStore.setPageNum(0);

        // Cleanup
        return function cleanup() {
            throttledSaveToServer.cancel();
            throttledSaveToLocalStorage.cancel();
        };

    }, []);

        let title = "CV Editor"
        if (documentName) {
            title = documentName + " - CV Editor";
        }

        return (
            <>
                <Helmet>
                    <title>{title}</title>
                </Helmet>
                {showProgressBar ? <SuspenseLoader/> :
                    <>
                        {
                            cvNotFound ?
                                <MuiAlert severity="error" elevation={6} variant="filled"
                                          sx={{
                                              borderRadius: '0px'
                                          }}>
                                    Unable to locate requested document
                                </MuiAlert> :
                                <>
                                    {
                                        editMode ?
                                            <Grid container spacing={0}>
                                                <Grid item xs={12} md={4}>
                                                    <ResumeFormEditor
                                                        editorContent={editorContent}
                                                        onEditorChange={onEditorChange}
                                                    />
                                                </Grid>
                                                <Grid item xs={12} md={8}>
                                                    <TemplateManager
                                                        reRenderPdf={reRenderPdf}
                                                        resume={editorContent}
                                                        templateType={templateType}
                                                        onSave={saveDocument}
                                                        displayIsSaving={displayIsSaving}
                                                        objectKey={objectKey}
                                                        documentName={documentName}
                                                        onEditToggle={onEditToggle}
                                                    />
                                                </Grid>
                                            </Grid> :
                                            <TemplateManager
                                                reRenderPdf={reRenderPdf}
                                                resume={editorContent}
                                                templateType={templateType}
                                                onSave={saveDocument}
                                                displayIsSaving={displayIsSaving}
                                                objectKey={objectKey}
                                                documentName={documentName}
                                                onEditToggle={onEditToggle}
                                            />
                                    }
                                </>
                        }
                    </>
                }
            </>
        );
});
export default CVEditor;