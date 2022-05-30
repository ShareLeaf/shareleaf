import React, {FC, useRef, useState} from 'react';

import { NavLink } from 'react-router-dom';

import {
  Avatar,
  Box,
  Button,
  Divider,
  Hidden,
  lighten,
  List,
  ListItem,
  ListItemText,
  Popover,
  Typography
} from '@mui/material';

import { styled } from '@mui/material/styles';
import ExpandMoreTwoToneIcon from '@mui/icons-material/ExpandMoreTwoTone';
import LockOpenTwoToneIcon from '@mui/icons-material/LockOpenTwoTone';
import {auth} from "../../../../auth/firebase-setup";
import { observer} from "mobx-react";
import {useStore} from "../../../../hooks/useStore";

const UserBoxButton = styled(Button)(
  ({ theme }) => `
        padding-left: ${theme.spacing(1)};
        padding-right: ${theme.spacing(1)};
`
);

const MenuUserBox = styled(Box)(
  ({ theme }) => `
        background: ${theme.colors.alpha.black[5]};
        padding: ${theme.spacing(2)};
`
);

const UserBoxText = styled(Box)(
  ({ theme }) => `
        text-align: left;
        padding-left: ${theme.spacing(1)};
`
);

const UserBoxLabel = styled(Typography)(
  ({ theme }) => `
        font-weight: ${theme.typography.fontWeightBold};
        color: ${theme.palette.secondary.main};
        display: block;
`
);

const UserBoxDescription = styled(Typography)(
  ({ theme }) => `
        color: ${lighten(theme.palette.secondary.main, 0.5)}
`
);

const HeaderUserbox: FC<any> = observer((props) => {
  const [open, setOpen] = useState<boolean>(false);
  const ref = React.createRef<any>()
  const store = useStore();

  const handleOpen = (): void => {
    setOpen(true);
  };

  const handleClose = (): void => {
    setOpen(false);
  };

  const handleSignOut = (): void => {
    auth.signOut().then(result => {
      store.userStore.reset()
      window.location.reload()
    }).catch(e => console.log(e));
  }


    const displayName = store.userStore.displayName
    const avatar = store.userStore.avatar
    const jobTitle = store.userStore.jobTitle

    return (
        <>
          <UserBoxButton color="secondary" ref={ref} onClick={handleOpen}>
            <Avatar variant="rounded" alt={displayName} src={avatar} />
            <Hidden mdDown>
              <UserBoxText>
                <UserBoxLabel variant="body1">{displayName}</UserBoxLabel>
                {/*<UserBoxDescription variant="body2">*/}
                {/*  {jobTitle}*/}
                {/*</UserBoxDescription>*/}
              </UserBoxText>
            </Hidden>
            <Hidden smDown>
              <ExpandMoreTwoToneIcon sx={{ ml: 1 }} />
            </Hidden>
          </UserBoxButton>
          <Popover
              anchorEl={ref.current}
              onClose={handleClose}
              open={open}
              anchorOrigin={{
                vertical: 'top',
                horizontal: 'right'
              }}
              transformOrigin={{
                vertical: 'top',
                horizontal: 'right'
              }}
          >
            <MenuUserBox sx={{ minWidth: 210 }} display="flex">
              <Avatar variant="rounded" alt={displayName} src={avatar} />
              <UserBoxText>
                <UserBoxLabel variant="body1">{displayName}</UserBoxLabel>
                {/*<UserBoxDescription variant="body2">*/}
                {/*  {jobTitle}*/}
                {/*</UserBoxDescription>*/}
              </UserBoxText>
            </MenuUserBox>
            {/*<Divider sx={{ mb: 0 }} />*/}
            {/*<List sx={{ p: 1 }} component="nav">*/}
            {/*  <ListItem button to="/management/profile/details" component={NavLink}>*/}
            {/*    <AccountBoxTwoToneIcon fontSize="small" />*/}
            {/*    <ListItemText primary="My Profile" />*/}
            {/*  </ListItem>*/}
            {/*  <ListItem*/}
            {/*    button*/}
            {/*    to="/dashboards/messenger"*/}
            {/*    component={NavLink}*/}
            {/*  >*/}
            {/*    <InboxTwoToneIcon fontSize="small" />*/}
            {/*    <ListItemText primary="Messenger" />*/}
            {/*  </ListItem>*/}
            {/*  <ListItem*/}
            {/*    button*/}
            {/*    to="/management/profile/settings"*/}
            {/*    component={NavLink}*/}
            {/*  >*/}
            {/*    <AccountTreeTwoToneIcon fontSize="small" />*/}
            {/*    <ListItemText primary="Account Settings" />*/}
            {/*  </ListItem>*/}
            {/*</List>*/}
            <Divider />
            <Box sx={{ m: 1 }}>
              <Button color="primary" fullWidth onClick={handleSignOut}>
                <LockOpenTwoToneIcon sx={{ mr: 1 }} />
                Sign out
              </Button>
            </Box>
          </Popover>
        </>
    );
});

export default HeaderUserbox;
