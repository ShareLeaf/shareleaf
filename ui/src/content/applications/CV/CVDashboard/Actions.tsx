import {useState, FC, useEffect} from 'react';

import {
    Box,
    Button,
    Typography, Divider, CardContent, Link
} from '@mui/material';
import { styled } from '@mui/material/styles';

import DeleteTwoToneIcon from '@mui/icons-material/DeleteTwoTone';
import EditIcon from "@mui/icons-material/Edit";
import LockOpenTwoToneIcon from '@mui/icons-material/LockOpenTwoTone';
import LockTwoToneIcon from '@mui/icons-material/LockTwoTone';
import * as React from "react";
import Dialog from "@mui/material/Dialog";
import DialogTitle from "@mui/material/DialogTitle";
import TextField from "@mui/material/TextField";
import {CVApi, RRCVMetadata} from "../../../../api";
import {observer} from "mobx-react";
import CancelSharpIcon from "@mui/icons-material/CancelSharp";
import {auth} from "../../../../auth/firebase-setup";
import {headerConfig} from "../../../../auth/headerConfig";

const ButtonError = styled(Button)(
  ({ theme }) => `
     background: ${theme.colors.error.main};
     color: ${theme.palette.error.contrastText};

     &:hover {
        background: ${theme.colors.error.dark};
     }
    `
);

const ButtonSuccess = styled(Button)(
    ({ theme }) => `
     background: ${theme.colors.success.main};
     color: ${theme.palette.success.contrastText};

     &:hover {
        background: ${theme.colors.success.dark};
     }
    `
);

const ButtonInfo = styled(Button)(
    ({ theme }) => `
     background: ${theme.colors.info.main};
     color: ${theme.palette.info.contrastText};

     &:hover {
        background: ${theme.colors.info.dark};
     }
    `
);

const ButtonWarning = styled(Button)(
    ({ theme }) => `
     background: ${theme.colors.warning.main};
     color: ${theme.palette.warning.contrastText};

     &:hover {
        background: ${theme.colors.warning.dark};
     }
    `
);

interface ActionsProps {
    record: RRCVMetadata,
    onGetPaginatedRecords: (paginated?: boolean) => void
}

const Actions: FC<ActionsProps> = observer((props) => {
  const [editDialogIsOpen, setEditDialogOpen] = useState<boolean>(false);
  const [deleteDialogOpen, setDeleteDialogOpen] = useState<boolean>(false);
  const [title, setTitle] = useState<string>("");
  const [tags, setTags] = useState<string[]>([]);
  const [isReadOnly, setReadOnly] = useState<boolean>(false);


  const handleEditDialogOpen = (): void => {
      setEditDialogOpen(true);
  };

  const handleDeleteDialogClose = (): void => {
      setDeleteDialogOpen(false);
  };

    const handleDeleteDialogOpen = (): void => {
        setDeleteDialogOpen(true);
    };

    const handleEditDialogClose = (): void => {
        setEditDialogOpen(false);
    };

  const handleDeleteListItem = (index: number): void => {
        const updatedTags = [...tags]
        updatedTags.splice(index, 1);
        setTags(updatedTags);
  }

    const handleLockRecord = () : void => {
      auth.onAuthStateChanged(user => {
          if (user) {
              user.getIdToken(false)
                  .then(token => {
                      const record = props.record;
                      record.read_only = !isReadOnly;
                      new CVApi(headerConfig(token))
                          .updateCVMetadata(record)
                          .then(() => {
                              props.onGetPaginatedRecords();
                          }).catch(e => console.log(e))
                  }).catch(e => console.log(e))
          }
      })
  }

    const handleAddListItem = (event: any): void => {
      if (event.key === 'Enter') {
          event.preventDefault();
          const item = event.target.value;
          let updatedTags = [...tags];
          updatedTags.push(item);
          setTags(updatedTags);
          event.target.value = "";
      }
  }

    const handleDeleteConfirmation = (): void => {
        setDeleteDialogOpen(false);
        auth.onAuthStateChanged(user => {
            if (user) {
                user.getIdToken(false)
                    .then(token => {
                        const record = props.record;
                        record.is_deleted = true;
                        new CVApi(headerConfig(token))
                            .updateCVMetadata(record)
                            .then(() => {
                                props.onGetPaginatedRecords();
                            }).catch(e => console.log(e))
                    }).catch(e => console.log(e))
            }
        })
    }

    const handleEditSubmit = (): void => {
        setEditDialogOpen(false);
        auth.onAuthStateChanged(user => {
            if (user) {
                user.getIdToken(false)
                    .then(token => {
                        const record = props.record;
                        record.tags = tags;
                        record.cv_title = title;
                        new CVApi(headerConfig(token))
                            .updateCVMetadata(record)
                            .then(() => {
                                props.onGetPaginatedRecords();
                            }).catch(e => console.log(e))
                    }).catch(e => console.log(e))
            }
        })
    }

    const handleTitleChange = (event: any) => {
        event.preventDefault();
        setTitle(event.target.value);
    }

    useEffect(() => {
        setTitle(props.record.cv_title);
        setTags(props.record.tags);
        setReadOnly(props.record.read_only);
    }, [])

  return (
    <>
      <Box display="flex" alignItems="center" justifyContent="space-between">

        <Box display="flex" alignItems="center">
          <ButtonSuccess
              onClick={handleEditDialogOpen}
            sx={{ ml: 1 }}
            startIcon={<EditIcon />}
            variant="contained"
          >
            Edit
          </ButtonSuccess>
            <Dialog
                onClose={handleEditDialogClose}
                open={editDialogIsOpen}
            >
                <DialogTitle>{"Edit document details"}</DialogTitle>
                <CardContent>
                    <Box
                        component="form"
                        sx={{
                            '& .MuiTextField-root': { m: 1, width: '60ch' },
                        }}
                        autoComplete="off"
                        onSubmit={handleEditDialogClose}
                    >
                        <Box>
                            <TextField
                                required
                                id={props.record.cv_key}
                                label="Required"
                                value={title}
                                helperText="Title"
                                variant="standard"
                                onChange={handleTitleChange}
                            />
                        </Box>
                        <Box sx={{ marginTop: '20px'}} >

                            <div className="rr-section-list-items">
                                <Typography variant="h5" sx={{
                                    paddingBottom: '10px',
                                    paddingLeft: '8px'
                                }}>Tags</Typography>

                                <ul>
                                    {tags && tags.map((item, itemIndex) => (
                                        <li key={"tags-" + itemIndex}>
                                            <span>{item}</span>
                                            <CancelSharpIcon
                                                className="rr-svg-delete-icon"
                                                color="error"
                                                onClick={() => handleDeleteListItem(itemIndex)}
                                            />
                                        </li>
                                    ))}
                                </ul>

                                <TextField
                                    id="standard-minor"
                                    label="Press enter after each entry"
                                    InputLabelProps={{
                                        shrink: true,
                                    }}
                                    name={`tags-items-list`}
                                    onKeyPress={(event) =>
                                        handleAddListItem(event)}
                                    variant="standard"
                                />
                            </div>
                        </Box>
                        <Box sx={{ marginTop: '50px'}} >
                            <Button
                                className={"form-dialog-btn"}
                                onClick={handleEditSubmit}
                                variant="contained"
                                color="success">Save</Button>
                        </Box>
                    </Box>
                </CardContent>
            </Dialog>
          <Divider
              sx={{
                mx: 2
              }}
              orientation="vertical"
              flexItem
          />
          <ButtonWarning
              onClick={handleLockRecord}
              sx={{ ml: 1 }}
              startIcon={isReadOnly ? <LockOpenTwoToneIcon /> : <LockTwoToneIcon/>}
              variant="contained"
          >
              {isReadOnly ? 'Unlock' : 'Lock'}
          </ButtonWarning>
          {/*<Divider*/}
          {/*    sx={{*/}
          {/*      mx: 2*/}
          {/*    }}*/}
          {/*    orientation="vertical"*/}
          {/*    flexItem*/}
          {/*/>*/}
          {/*<ButtonInfo*/}
          {/*    sx={{ ml: 1 }}*/}
          {/*    startIcon={<AccountTreeIcon />}*/}
          {/*    variant="contained"*/}
          {/*>*/}
          {/*  Lineage*/}
          {/*</ButtonInfo>*/}
          <Divider
              sx={{
                mx: 2
              }}
              orientation="vertical"
              flexItem
          />
          <ButtonError
              sx={{ ml: 1 }}
              startIcon={<DeleteTwoToneIcon />}
              variant="contained"
              onClick={handleDeleteDialogOpen}
          >
            Delete
          </ButtonError>
            <Dialog
                onClose={handleDeleteDialogClose}
                open={deleteDialogOpen}
            >
                <CardContent>
                    <Box
                        sx={{
                            padding: '2rem',
                            '& .MuiTextField-root': { m: 1, width: '60ch' },
                        }}
                    >
                        <Box>
                            <Typography
                                component="span"
                                variant="body1"
                                color="text.primary"
                                gutterBottom
                                noWrap
                            >
                                Are you sure you want to delete this file?
                            </Typography>
                        </Box>
                        <Box sx={{ marginTop: '50px', textAlign: 'center'}} >
                            <Button
                                className={"form-dialog-btn"}
                                onClick={handleDeleteConfirmation}
                                variant="contained"
                                color="error">Confirm</Button>
                        </Box>
                    </Box>
                </CardContent>
            </Dialog>
        </Box>
      </Box>
    </>
  );
});

export default Actions;
