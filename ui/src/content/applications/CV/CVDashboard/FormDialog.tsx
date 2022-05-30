import React, {FC, useEffect, useState} from "react";
import Dialog from "@mui/material/Dialog";
import DialogTitle from "@mui/material/DialogTitle";
import {Box, CardContent} from "@mui/material";
import TextField from "@mui/material/TextField";
import ChipInput from "material-ui-chip-input";
import { observer} from "mobx-react";
import {Button} from "@material-ui/core";


const FormDialog: FC<any> = observer((props) => {
    const defaultTitle = "Untitled";
    const titleId = "standard-title";
    const helpText = "Give your CV a title";
    const [tags, setTags] = useState<string[]>([]);
    const [title, setTitle] = useState<string>(defaultTitle);
    
    useEffect(() => {
        setTitle(props.title);
        setTags(props.tags);
    }, [])

    const handleAddChip = (item: string): void => {
        const updatedTags = [...tags]
        updatedTags.push(item)
        setTags(updatedTags);
    }

    const handleDeleteChip = (item: string, index: number): void => {
        const updatedTags = [...tags]
        updatedTags.splice(index, 1);
        setTags(updatedTags);
    }

    const handleOnChange = (e: any) => {
        e.preventDefault()
        setTitle(e.target.value);
    }

    const handleClose = (e: any): void => {
        e.preventDefault()
        if (title.length === 0) {
            setTitle(defaultTitle);
        }
        props.onClose({
            title: title,
            tags: tags
        })
    };

        return (
            <Dialog
                onClose={handleClose}
                open={props.open}
            >
                <DialogTitle>{props.cvCreateTitle}</DialogTitle>
                <CardContent>
                    <Box
                        component="form"
                        sx={{
                            '& .MuiTextField-root': { m: 1, width: '60ch' },
                        }}
                        autoComplete="off"
                        onSubmit={handleClose}
                    >
                        <Box>
                            <TextField
                                required
                                id={titleId}
                                label="Required"
                                defaultValue={title}
                                helperText={helpText}
                                variant="standard"
                                onChange={handleOnChange}
                            />
                        </Box>
                        <Box sx={{ marginTop: '20px'}} >
                            <ChipInput
                                label="Tags"
                                helperText="Press enter after each entry"
                                value={tags}
                                onAdd={(chip) => handleAddChip(chip)}
                                onDelete={(chip, index) => handleDeleteChip(chip, index)}
                            />
                        </Box>
                        <Box sx={{ marginTop: '50px'}} >
                            <Button
                                className={"form-dialog-btn"}
                                type="submit"
                                variant="contained"
                                color="primary">OK</Button>
                        </Box>
                    </Box>
                </CardContent>
            </Dialog>
        );
});
export default FormDialog;