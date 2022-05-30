import {
  Box,
  Card,
  Typography,
  Container,
  Divider,
  Button,
} from '@mui/material';
import { Helmet } from 'react-helmet-async';
import { signInWithPopup, GoogleAuthProvider, getAuth, setPersistence, indexedDBLocalPersistence } from "firebase/auth";

import { styled } from '@mui/material/styles';
import {auth} from "../../../auth/firebase-setup";
import {SvgIcon} from "../Landing/common/SvgIcon";
import {LogoContainerCentered} from "../Landing/components/Header/styles";
import {observer} from "mobx-react";
import React, {FC, useState} from "react";
import firebase from "firebase/compat/app";
import SuspenseLoader from "../../../components/SuspenseLoader";
import { UserApi} from "../../../api";
import {headerConfig} from "../../../auth/headerConfig";
import {useStore} from "../../../hooks/useStore";

const MainContent = styled(Box)(
  ({ theme }) => `
    height: 90%;
    display: flex;
    flex: 1;
    overflow: auto;
    flex-direction: column;
    align-items: center;
    justify-content: center;
`
);

const Login: FC<any> = observer((props) => {
    const [showProgress, setShowProgress] = useState<boolean>(false);
    const store = useStore();

    const handleShowProgress = () => {
        setShowProgress(true);
    }

    const handleHideProgress = () => {
        setShowProgress(false);
    }

    /**
     * Sign in user using Google login pop screen. After login, persist the user
     * object in indexDB so that the session is not terminated if the user refreshes
     * the page or closes the browser.
     */
    const signInWithGoogle = () => {
        setPersistence(auth, indexedDBLocalPersistence)
            .then(() => {
                // In local persistence will be applied to the signed in Google user
                return signInWithPopup(auth, new GoogleAuthProvider())
                    .then((result) => {
                        if (result.user) {
                            handleShowProgress();
                            store.userStore.setUser(result.user as firebase.User)
                            registerUser()
                        } else {
                            store.userStore.setUser(null)
                        }
                    }).catch(e => console.log(e));
            })
            .catch((error) => {
                // Handle Errors here.
                const errorCode = error.code;
                const errorMessage = error.message;
            });
    }

    /**
     * Check if the current user is registered. If not, register them. Once
     * registration has been verified or completed, disable the progress indicator and
     * route the user to the dashboard.
     *
     */
    const registerUser = () => {
        auth.onAuthStateChanged(user => {
            if (user) {
                user.getIdTokenResult(false)
                    .then(tokenResult => {
                        if (tokenResult.claims.registered) {
                            handleHideProgress();
                            window.location.reload()
                        } else {
                            new UserApi(headerConfig(tokenResult.token))
                                .createUser()
                                .then(result => {
                                    handleHideProgress();
                                    window.location.reload()
                                }).catch(e => console.log(e))
                        }
                    }).catch(e => console.log(e))
            }
        })
    }
        return (
            <>
                <Helmet>
                    <title>ResumeRepo - Sign In</title>
                </Helmet>
                {
                    showProgress ? <SuspenseLoader/> :
                        <MainContent>
                            <Container maxWidth="xs">
                                <Card sx={{textAlign: 'center', mt: 3, p: 4, width: '100%', paddingBottom: '80px'}}>
                                    <Box sx={{width: '100%'}}>
                                        <LogoContainerCentered to="/" aria-label="homepage">
                                            <SvgIcon src="logo.svg" width="186px" height="100%"/>
                                        </LogoContainerCentered>
                                        <Box sx={{width: '100%', marginTop: '20px'}}>
                                            <Typography
                                                variant="h6"
                                                color="text.secondary"
                                                fontWeight="normal"
                                                sx={{mb: 4, marginBottom: '10px'}}
                                            >
                                                ResumeRepo does not store your login information.
                                            </Typography>
                                        </Box>

                                        <Divider sx={{my: 4}}>Sign in with</Divider>
                                        <Box
                                            display="flex"
                                            alignItems="center"
                                            mr={2}
                                            sx={{width: '100%'}}
                                        >
                                            <Button
                                                sx={{width: '100%', marginTop: '10px'}}
                                                size="large"
                                                startIcon={<img alt="ResumeRepo" height={18}
                                                                src="/static/images/logo/google.svg"/>}
                                                variant="outlined"
                                                onClick={signInWithGoogle}>
                                                Sign in with Google
                                            </Button>
                                        </Box>
                                        {/*<Box*/}
                                        {/*    display="flex"*/}
                                        {/*    alignItems="center"*/}
                                        {/*    mr={2}*/}
                                        {/*    sx={{ width: '100%' }}*/}
                                        {/*>*/}
                                        {/*    <Button*/}
                                        {/*        sx={{ width: '100%', marginTop: '15px' }}*/}
                                        {/*        size="large"*/}
                                        {/*        startIcon={<img alt="ResumeRepo" height={20} src="/static/images/logo/apple.svg" />}*/}
                                        {/*        variant="outlined">*/}
                                        {/*      Sign in with Apple*/}
                                        {/*    </Button>*/}
                                        {/*</Box>*/}
                                    </Box>
                                </Card>
                            </Container>
                        </MainContent>
                }
            </>
        );
});
export default Login;
