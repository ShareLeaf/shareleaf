import * as React from "react";
import {RRCVTemplate, RRResume} from "../../../../api";
import {Box, Container, Grid, IconButton, Tooltip} from "@mui/material";
import {LogoContainer} from "../../../pages/Landing/components/Header/styles";
import {SvgIcon} from "../../../pages/Landing/common/SvgIcon";
import EditIcon from "@mui/icons-material/Edit";
import SaveIcon from "@mui/icons-material/Save";
import DownloadIcon from "@mui/icons-material/Download";
import {styled} from "@mui/material/styles";
import { pdf } from '@react-pdf/renderer'
import {FC} from "react";
import AppDocument from "../Template/Resume/app";
import techData from './en.json';
import {TechProfile, TechResume} from "../Template/Resume/types";
import { saveAs } from 'file-saver';
import PDF from "../Template/Resume/app/PDF";
import {observer} from "mobx-react";
import {useStore} from "../../../../hooks/useStore";

interface TemplateManagerProps {
    reRenderPdf: number,
    displayIsSaving: boolean,
    objectKey: string,
    documentName: string,
    resume?: RRResume,
    templateType: RRCVTemplate,
    onSave: () => void,
    onEditToggle: () => void;
}

const DownloadIconButton = styled(IconButton)(
    ({ theme }) => `
        &:hover: { background: ${theme.colors.warning.lighter} };
        color: ${theme.palette.warning.main};
        color: "inherit";
        size: "small";
`
);

const SaveIconButton = styled(IconButton)(
    ({ theme }) => `
        &:hover: { background: ${theme.colors.success.lighter} };
        color: ${theme.palette.success.main};
        color: "inherit";
        size: "small";
`
);

const EditIconButton = styled(IconButton)(
    ({ theme }) => `
        &:hover: { background: ${theme.colors.error.lighter} };
        color: ${theme.palette.error.main};
        color: "inherit";
        size: "small";
`
);
export const TemplateManager: FC<TemplateManagerProps> = observer((props) => {
    const store = useStore();
    const handleDownloadPdf = async ()  => {
        const blob = await pdf(
            <PDF
                techResume={techData.techResume as TechResume}
                techProfile={techData.techProfile as TechProfile}
                resume={store.documentStore.content}
            />
        ).toBlob();
        saveAs(blob, props.documentName);
    };

    /**
     * Render the editor in the following order:
     *  1. Show the progress spinner until either we reach a timeout or our document
     *  store gets updated from the server response.
     *  2. If there is no document to show after the timeout, display the error message
     */
    return (
        <Container maxWidth="lg">
            <Grid
                container
                direction="row"
                justifyContent="center"
                alignItems="stretch"
                spacing={0}
            >
                <Grid item xs={12}>
                    <Box
                        sx={{
                            paddingTop: '2rem',
                            paddingBottom: '5px',
                            margin: 'auto',
                            maxWidth: '8.5in',
                            display: 'flex',
                        }}>
                        <Grid item xs={9}>
                            <LogoContainer to="/" aria-label="homepage">
                                <SvgIcon src="logo.svg" width="186px" height="100%" />
                            </LogoContainer>
                        </Grid>
                        <Grid item xs={1}>
                            <Tooltip arrow title="Edit">
                            <EditIconButton onClick={props.onEditToggle}>
                                <EditIcon
                                    fontSize="medium"
                                />
                            </EditIconButton>
                        </Tooltip>
                        </Grid>
                        <Grid item xs={1}>
                            <Tooltip arrow title="Save">
                            <SaveIconButton onClick={props.onSave}>
                                <SaveIcon fontSize="medium"/>
                            </SaveIconButton>
                            </Tooltip>

                        </Grid>
                        <Grid item xs={1}>
                            {
                                props.resume &&
                                <Tooltip arrow title="Download PDF">
                                    <DownloadIconButton onClick={handleDownloadPdf}>
                                        <DownloadIcon fontSize="medium" />
                                    </DownloadIconButton>
                                </Tooltip>
                            }
                        </Grid>
                    </Box>
                </Grid>
                <Grid item xs={12}>
                    <Box sx={{
                        maxWidth: '8.5in',
                        paddingTop: '0rem',
                        paddingBottom: '4rem',
                        margin: 'auto',
                        display: 'flex',
                        justifyContent: 'center',
                        maxHeight: '100vh',
                        overflow: 'auto'
                    }}>
                        {props.templateType === RRCVTemplate.PROFESSIONAL &&
                            <AppDocument
                                reRenderPdf={props.reRenderPdf}
                                techResume={techData.techResume as TechResume}
                                techProfile={techData.techProfile as TechProfile}
                            />
                        }
                    </Box>
                </Grid>
                {/*<Grid item xs={12}>*/}
                {/*    <Box sx={{*/}
                {/*        paddingTop: '0rem',*/}
                {/*        paddingBottom: '4rem',*/}
                {/*        margin: 'auto',*/}
                {/*        display: 'flex',*/}
                {/*        justifyContent: 'center',*/}
                {/*        maxHeight: '100vh',*/}
                {/*        overflow: 'auto'*/}
                {/*    }}>*/}
                {/*        {props.templateType === RRCVTemplate.PROFESSIONAL &&*/}
                {/*            <Professional*/}
                {/*                content={props.resume}*/}
                {/*            />*/}
                {/*        }*/}
                {/*    </Box>*/}
                {/*</Grid>*/}


            </Grid>
        </Container>);
});