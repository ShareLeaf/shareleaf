import {Helmet} from 'react-helmet-async';
import { Card, Container, Grid} from '@mui/material';

import React, {FC, useEffect, useState} from "react";
import {observer} from "mobx-react";
import {auth} from "../../../../auth/firebase-setup";
import {
    CVApi,
    RRCreateFromTemplateRequest,
    RRCVMetadata,
    RRCVTemplate,
    RRObjectType,
    RRPaginatedRequest
} from "../../../../api";
import {headerConfig} from "../../../../auth/headerConfig";
import SuspenseLoader from "../../../../components/SuspenseLoader";
import DocumentListTable from "./DocumentListTable";
import {useStore} from "../../../../hooks/useStore";
import TemplateSelector from "./TemplateSelector";
import {PageDefaults} from "../../../../store/Store";

interface CVDashboardProps {
    cvType: RRObjectType
}

const CVDashboard: FC<CVDashboardProps> = observer((props) =>  {
    const store = useStore();
    const [showProgress, setShowProgress] = useState<boolean>(false);
    const [title, setTitle] = useState<string>("");
    const [records, setRecords] = useState<Array<RRCVMetadata>>([]);

    const handleShowProgress = () => {
        setShowProgress(true);
    }

    const hideProgress = () => {
        setShowProgress(false);
    }

    const setCvProperties = () => {
        let title = ""
        if (props.cvType === RRObjectType.TYPE_R) {
            title = "Resumes"
            setRecords(store.resumeStore.records);
        } else {
            setRecords(store.coverLetterStore.records);
            title = "Cover Letters"
        }
        setTitle(title);
        getPaginatedRecords();
    }

    /**
     * Create a new blank document and re-direct to the editor on success
     *
     */
    const createDocument = (templateType: RRCVTemplate): void => {
        handleShowProgress();
        auth.onAuthStateChanged(user => {
            if (user) {
                user.getIdToken(false)
                    .then(token => {
                        const request : RRCreateFromTemplateRequest = {
                            template_name: templateType
                        }
                        new CVApi(headerConfig(token))
                            .createResumeFromTemplate(request)
                            .then(result => {
                                store.documentStore.setObjectName(result.data.object_key)
                                window.location.pathname = "/document/" + result.data.object_key;
                                hideProgress();
                            }).catch(e => console.log(e))
                    }).catch(e => console.log(e))
            }
        })
    }

    /**
     * Fetch all user profile information such as the data displayed on the landing page in the
     * dashboard and relevant flags.
     *
     */
    const getPaginatedRecords = (paginated?: boolean) => {
        // TODO: find a way to only call the server if there are new records
            handleShowProgress();
            auth.onAuthStateChanged(user => {
                if (user) {
                    user.getIdToken(false)
                        .then(token => {
                            // TODO: include sort params
                            let request : RRPaginatedRequest = null;
                            if (props.cvType === RRObjectType.TYPE_R) {
                                request = {
                                    limit: store.resumeStore.pageSize,
                                    next_page_num: store.resumeStore.pageNum,
                                    object_type: RRObjectType.TYPE_R
                                };
                            } else {
                                request = {
                                    limit: store.coverLetterStore.pageSize,
                                    next_page_num: store.coverLetterStore.pageNum,
                                    object_type: RRObjectType.TYPE_R
                                };
                            }
                            if (request.limit < 0) {
                                request.limit = PageDefaults.pageSize;
                            }
                            new CVApi(headerConfig(token))
                                .getAllPaginatedCVMetadata(request)
                                .then(result => {
                                    // Save all to resume store until cover letter support is added
                                    if (paginated) {
                                        store.resumeStore.setRecords(result.data.records, true);
                                    } else {
                                        store.resumeStore.setRecords(result.data.records);
                                    }
                                    setRecords(store.resumeStore.records);

                                    store.resumeStore.setTotalRecords(result.data.total_records);
                                    hideProgress()
                                }).catch(e => console.log(e))
                        }).catch(e => console.log(e))
                }
            })
    }

    useEffect(() => {
        hideProgress()
        setCvProperties()
    }, [])

    let cvStore = store.resumeStore;
    if (props.cvType === RRObjectType.TYPE_CL) {
        cvStore = store.coverLetterStore;
    }

    return (
        <>
              <Helmet>
                <title>{title}</title>
              </Helmet>
                {
                    showProgress ? <SuspenseLoader/> :
                        <Container maxWidth="lg">
                            <Grid
                                sx={{marginTop: '2rem', marginBottom: '4rem'}}
                                container
                                direction="row"
                                justifyContent="center"
                                alignItems="stretch"
                                spacing={3}
                            >
                                <Grid item xs={12} sx={{marginBottom: '2rem'}}>
                                    <TemplateSelector onDocumentCreate={createDocument}/>
                                </Grid>
                                <Grid item xs={12}>
                                    <Card>
                                        <DocumentListTable
                                            records={records}
                                            store={cvStore}
                                            onGetPaginatedRecords={getPaginatedRecords}
                                        />
                                    </Card>
                                </Grid>
                            </Grid>
                        </Container>
                }
            </>)
});

export default CVDashboard;
