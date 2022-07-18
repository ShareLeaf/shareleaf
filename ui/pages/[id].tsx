import {
    Alert,
    Box,
    Container,
    Grid,
    Typography,
    Card,
    Divider,
    Button,
    CardActions,
    styled, Tooltip
} from '@mui/material';
import React, {FC, useEffect, useState} from "react";
import VideoElement from "src/content/Media/VideoElement";
import Loader from 'src/components/Loader';
import Common from "@/content/Common";
import ImageElement from "src/content/Media/ImageElement";
import Head from "next/head";
import ShareTwoToneIcon from '@mui/icons-material/ShareTwoTone';
import {copyToClipBoard} from "@/content/utils/utils";
import { headerConfig } from '@/api/headerConfig';
import { ContentApi, SLContentMetadata } from '@/api/api';
import Snackbar from '@mui/material/Snackbar';

const OverviewWrapper = styled(Box)(
    ({ theme }) => `
    background: ${theme.palette.common.white};
    flex: 1;
`
);

const CardActionsWrapper = styled(CardActions)(
    ({ theme }) => `
     background: ${theme.colors.alpha.black[5]};
     padding-left: ${theme.spacing(3)};
     padding-right: ${theme.spacing(3)};
     padding-top: ${theme.spacing(1)};
     padding-bottom: ${theme.spacing(3)};
     // float: right;
`
);
const ButtonWrapper = styled(Button)(
    ({ theme }) => `
    color: ${theme.colors.primary.main};
    background: ${theme.colors.alpha.white[70]};
    font-weight: normal;
    
    &:hover {
      color: ${theme.colors.alpha.black[100]};
      background: ${theme.colors.alpha.white[100]};
    }
`
);

const DividerWrapper = styled(Divider)(
    ({ theme }) => `
    background: ${theme.colors.alpha.black[10]};
`
);

const BoxWrapper = styled(Box)(
    ({ theme }) => `
    border-radius: ${theme.general.borderRadius};
    background: ${theme.colors.alpha.black[10]};
`
);
export async function getServerSideProps(context) {
    let props = {}
    try {
        props = (await new ContentApi().getMetadata(context.query.id)).data;
    } catch (e) {
        console.log(e)
    }
    return {
        props: props
    };
}

const Media: FC<SLContentMetadata> = (props) => {
    const [metadata, setMetadata] = useState<SLContentMetadata | undefined>(undefined);
    const [showError, setShowError] = useState<boolean>(false);
    const [showInvalidUrlError, setShowInvalidUrlError] = useState<boolean>(false);
    const [showInProgress, setShowInProgress] = useState<boolean>(false);
    const [open, setOpen] = useState<boolean>(false);
    const [shareOpen, setShareOpen] = useState<boolean>(false);

    const handleOpen = () => {
        if (metadata.media_type === "image") {
            setOpen(!open);
        }
    }

    const handleShare = async () : Promise<void> => {
         new ContentApi(headerConfig()).incrementShareCount({uid: props.content_id})
             .then()
             .catch(e => console.log(e));
        await copyToClipBoard(props.shareable_link)
        setShareOpen(true);
    }


    const handleShareClose = (_event: React.SyntheticEvent | Event, reason?: string) => {
        if (reason === 'clickaway') {
            return;
        }
        setShareOpen(false);
    };

    useEffect(() => {
        setShowInProgress(false);
        setShowError(false);
        setShowInvalidUrlError(false);
        if (props && props.error) {
            setShowError(true);
        }
        else if (props.invalid_url) {
            setShowInvalidUrlError(true);
        } else {
            if (props.processed) {
                setMetadata(props);
            } else {
                setShowInProgress(true);
            }
        }
    }, [])

    let component = undefined;
    if (metadata) {
        component = (
            <OverviewWrapper>
                <Common
                    title={metadata.title}
                    children={
                        <Container maxWidth="md" sx={{ padding: 0, mb: 10, mt: 3}}>
                            <Grid
                                justifyContent="center"
                                alignItems="center"
                                container
                                sx={{
                                    textAlign: "center",
                                    p: 0
                                }}
                            >
                                <Grid item md={8} xs={12}>
                                <Card>
                                    <CardActionsWrapper
                                        sx={{
                                            display: { xs: 'block', md: 'flex' },
                                            alignItems: 'right',
                                            justifyContent: 'right',
                                            float: 'right',
                                            width: '100%'
                                        }}
                                    >
                                        <Box sx={{padding: 0, float: 'right'}}>
                                            <Tooltip title={'Copy URL to Share'} arrow>
                                            <Button startIcon={<ShareTwoToneIcon />}
                                                    onClick={handleShare}
                                                    variant="outlined">
                                                {('Share')}
                                            </Button>
                                            </Tooltip>
                                            <Snackbar
                                                anchorOrigin={{
                                                    vertical: 'top',
                                                    horizontal: 'center',
                                                }}
                                                open={shareOpen}
                                                autoHideDuration={6000}
                                                onClose={handleShareClose}>
                                                <Alert
                                                    onClose={handleShareClose}
                                                    severity="success"
                                                    sx={{ width: '100%' }}>
                                                    Link Copied!
                                                </Alert>
                                            </Snackbar>
                                        </Box>
                                    </CardActionsWrapper>
                                    {metadata.media_type=== "video" &&
                                        <VideoElement
                                            thumbnail={metadata.image_url}
                                            encoding={metadata.encoding}
                                            videoSrc={metadata.video_url}
                                            audioSrc={metadata.audio_url}
                                            title={metadata.title}
                                        />
                                    }
                                    {metadata.media_type=== "image" &&
                                            <ImageElement
                                                handleOpen={handleOpen}
                                                imageSrc={metadata.image_url}
                                                title={metadata.title}
                                            />
                                    }
                                    <Divider />
                                    <CardActionsWrapper
                                        sx={{
                                            width: '100%',
                                            display: { xs: 'flex', md: 'flex' },
                                            alignItems: 'center',
                                            justifyContent: 'space-between'
                                        }}
                                    >
                                        <BoxWrapper
                                            sx={{width: '100%'}}
                                            display="flex"
                                            alignItems="stretch"
                                            justifyContent="space-between"
                                            mt={2}
                                            p={1}
                                        >
                                            <Box display="flex" alignItems="center">
                                                <ButtonWrapper
                                                    size="small"
                                                    color="secondary"
                                                    variant="text"
                                                >
                                                    <b>{metadata.share_count}</b>&nbsp;{('shares')}
                                                </ButtonWrapper>
                                                <DividerWrapper
                                                    sx={{
                                                        mx: 2
                                                    }}
                                                    orientation="vertical"
                                                    flexItem
                                                />
                                                <Typography variant="body2" color="text.primary">
                                                    <b>{metadata.view_count}</b> {('views')}
                                                </Typography>
                                            </Box>
                                        </BoxWrapper>
                                        <Box sx={{width: "25px", mt: 2, ml: 3}}
                                             component="img"
                                             alt="ShareLeaf"
                                             src="/favicon-32x32.png"
                                        />

                                    </CardActionsWrapper>
                                </Card>
                                </Grid>
                            </Grid>
                        </Container>
                    }/>
            </OverviewWrapper>
        );
        if (open) {
            return (
                <dialog
                    className="image-dialog"
                    style={{ position: "absolute", margin: 0, padding: 0, width: '100%' }}
                    open
                    onClick={handleOpen}
                >
                    <img
                        style={{margin: 0, padding: 0}}
                        src={metadata.image_url}
                        onClick={handleOpen}
                        alt={metadata.title}
                    />
                </dialog>
            )
        }
    }
    else {
        if (showInProgress) {
            component = <Alert severity="info">
                We are currently processing this url. Please check back in a few minutes.
            </Alert>
        }
        else if (showError) {
            component = <Alert severity="error">
                Sorry, could not find the page you're looking for :(
            </Alert>
        }  else if (showInvalidUrlError) {
            component = <Alert severity="warning">
                Sorry, we were not able to process that URL. It may not be supported at the moment. Please DM us the link here
                https://twitter.com/share_leaf and we'll investigate.
            </Alert>
        } else {
            component = <Loader/>
        }
    }
    return <>
        <Head>
            <title>{props.title}</title>
            <meta property="og:title" content={props.title} key="title"/>
            <meta property="og:description" content={props.description} key="description"/>
            <meta property="og:image" content={props.image_url} key="image"/>
        </Head>
        {component}
    </>

}

export default Media;