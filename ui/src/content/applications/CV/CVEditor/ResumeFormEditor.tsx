import * as React from "react";
import Box from "@mui/material/Box";
import {
    Accordion,
    AccordionDetails, AccordionSummary,
    Card,
    CardContent,
    Divider,
    Grid,
    Typography,
    CardHeader,
    Button
} from "@mui/material";
import TextField from "@mui/material/TextField";
import ExpandMoreIcon from "@mui/icons-material/ExpandMore";
import AddIcon from '@mui/icons-material/Add';
import DeleteIcon from '@mui/icons-material/Delete';
import {RRResume} from "../../../../api";
import {parseDate} from "../Template/date";
import {number} from "prop-types";
import CancelSharpIcon from '@mui/icons-material/CancelSharp';
import moment from "moment";
import {FC} from "react";

interface ResumeFormProps {
    editorContent: RRResume,
    onEditorChange: (editorContent: RRResume) => void
}

const ResumeFormEditor: FC<ResumeFormProps> = (props) => {
    const sanitize = (_content: any): any => {
        if (_content) {
            return _content;
        } else if (_content instanceof number) {
            return 0;
        } else if (_content instanceof Array) {
            return [];
        } else {
            return "";
        }
    }

    const handleDelete = (id: string) => {
        // Delete a list item
        const _content = {...props.editorContent};
        const keys = id.split("-");
        const section = keys[0], index = keys[1];
        const blocks = _content[section]
        blocks.splice(index, 1)

        _content[section] = blocks;
        props.onEditorChange(_content);
    }

    const handleInsert = (id: string) => {
        // Insert an item into a list
        const _content = {...props.editorContent};
        let blocks = _content[id]
        if (blocks) {
            blocks.push({});
        } else {
            blocks = [{}];
        }
        _content[id] = blocks;
        props.onEditorChange(_content);
    }

    const handleAddListItem = (event: any, id: string): void => {
        if (event.key === 'Enter') {
            event.preventDefault();
            const _content = {...props.editorContent};
            const item = event.target.value;
            const keys = id.split("-");
            const section = keys[0], sectionIndex = keys[1];
            let updatedItems = [];
            if (_content[section][sectionIndex].items) {
                updatedItems = [..._content[section][sectionIndex].items]
            }
            updatedItems.push(item)
            _content[section][sectionIndex].items = updatedItems;
            props.onEditorChange(_content);
            event.target.value = "";
        }
    }

    const handleDeleteListItem = (item: string, index: number, id: string): void => {
        const _content = {...props.editorContent};
        const keys = id.split("-");
        const section = keys[0], sectionIndex = keys[1];
        const updatedItems = [..._content[section][sectionIndex].items]
        updatedItems.splice(index, 1);
        _content[section][sectionIndex].items = updatedItems;
        props.onEditorChange(_content);
    }

    /**
     * Handle input change by parsing the changed field value and updating
     * the local state.
     * @param event
     */
    const handleChange = (event) => {
        event.preventDefault();
        const keys = event.target.name.split("-");
        const section = keys[0], field = keys[1];
        let index = -1;
        if (keys.length > 2) { index = keys[2]; }
        let value = event.target.value;
        if (field.includes("_date") && value) {
            value = moment(value).format("YYYY-MM-DD 00:00:00.000")
        }
        const _content = {...props.editorContent};
        if (index >= 0) {
            _content[section][index][field] = value;
        } else {
            _content[section][field] = value;
        }
        props.onEditorChange(_content);
    };

    const handleProfileChange = (event) => {
        event.preventDefault();
        const _content = {...props.editorContent};
        _content.profile[event.target.name] = event.target.value;
        props.onEditorChange(_content);
    };


    const buildGenericFormSection = (cvSection: string): JSX.Element => {
        if (props.editorContent) {
            const elements = props.editorContent[cvSection].map((sectionItem, sectionIndex) => {
                const parentKey = cvSection + "-form-" + sectionIndex;
                let institutionHeader = "Institution";
                let institutionField = "institution";
                if (sectionItem.title) {
                    institutionHeader = "Title";
                    institutionField = "title";
                }
                return (
                    <div key={parentKey}>
                        <Card>
                            <CardHeader
                                action={
                                    <Button
                                        onClick={() => handleDelete(`${cvSection}-${sectionIndex}`)}
                                        className="rr-section-control-btn"
                                        startIcon={<DeleteIcon />}
                                    >
                                        Delete
                                    </Button>
                                }
                            />
                            <CardContent>
                                <Grid item xs={12}>
                                    <Box
                                        component="form"
                                        sx={{
                                            '& .MuiTextField-root': { m: 1, width: '94%' },
                                        }}
                                        noValidate
                                        autoComplete="off"
                                    >
                                        {sectionItem.position &&
                                            <div>
                                                <TextField
                                                    id="standard-position"
                                                    label="Position"
                                                    value={sanitize(sectionItem.position)}
                                                    InputLabelProps={{
                                                        shrink: true,
                                                    }}
                                                    name={`${cvSection}-position-${sectionIndex}`}
                                                    onChange={handleChange}
                                                    variant="standard"
                                                />
                                            </div>
                                        }
                                        <div>
                                            <TextField
                                                id="standard-institution"
                                                label={institutionHeader}
                                                value={sanitize(sectionItem[institutionField])}
                                                InputLabelProps={{
                                                    shrink: true,
                                                }}
                                                name={`${cvSection}-${institutionField}-${sectionIndex}`}
                                                onChange={handleChange}
                                                variant="standard"
                                            />
                                        </div>
                                        <div>
                                            <TextField
                                                id="standard-location"
                                                label="Location"
                                                value = {sanitize(sectionItem.location)}
                                                InputLabelProps={{
                                                    shrink: true,
                                                }}
                                                name={`${cvSection}-location-${sectionIndex}`}
                                                onChange={handleChange}
                                                variant="standard"
                                            />
                                        </div>

                                        <div>
                                            <TextField
                                                id="standard-url"
                                                label="Link"
                                                value={sanitize(sectionItem.url)}
                                                InputLabelProps={{
                                                    shrink: true,
                                                }}
                                                name={`${cvSection}-url-${sectionIndex}`}
                                                onChange={handleChange}
                                                variant="standard"
                                            />
                                        </div>
                                    </Box>
                                    {cvSection === "education" &&
                                        <Box
                                            component="form"
                                            sx={{
                                                '& .MuiTextField-root': {m: 1, width: '46%'},
                                            }}
                                            noValidate
                                            autoComplete="off"
                                        >
                                            <div>
                                                <TextField
                                                    id="standard-major"
                                                    label="Major"
                                                    value={sanitize(sectionItem.major)}
                                                    InputLabelProps={{
                                                        shrink: true,
                                                    }}
                                                    name={`${cvSection}-major-${sectionIndex}`}
                                                    onChange={handleChange}
                                                    variant="standard"
                                                />
                                                <TextField
                                                    id="standard-minor"
                                                    label="Minor"
                                                    value={sanitize(sectionItem.minor)}
                                                    InputLabelProps={{
                                                        shrink: true,
                                                    }}
                                                    name={`${cvSection}-minor-${sectionIndex}`}
                                                    onChange={handleChange}
                                                    variant="standard"
                                                />
                                                <TextField
                                                    id="standard-gpa"
                                                    type="number"
                                                    label="GPA"
                                                    value={sanitize(sectionItem.gpa)}
                                                    InputLabelProps={{
                                                        shrink: true,
                                                    }}
                                                    name={`${cvSection}-gpa-${sectionIndex}`}
                                                    onChange={handleChange}
                                                    variant="standard"
                                                />
                                            </div>
                                        </Box>
                                    }
                                    <Box
                                        component="form"
                                        sx={{
                                            '& .MuiTextField-root': { m: 1, width: '25ch' },
                                        }}
                                        noValidate
                                        autoComplete="off"
                                    >
                                        <div>
                                            <TextField
                                                id="standard-start-date"
                                                label="Start Data"
                                                type="date"
                                                value={sanitize(parseDate(sectionItem.start_date))}
                                                InputLabelProps={{
                                                    shrink: true,
                                                }}
                                                name={`${cvSection}-start_date-${sectionIndex}`}
                                                onChange={handleChange}
                                                variant="standard"
                                            />

                                            <TextField
                                                id="standard-end-date"
                                                label="End Data"
                                                type="date"
                                                value={sanitize(parseDate(sectionItem.end_date))}
                                                InputLabelProps={{
                                                    shrink: true,
                                                }}
                                                name={`${cvSection}-end_date-${sectionIndex}`}
                                                onChange={handleChange}
                                                variant="standard"
                                            />
                                        </div>
                                    </Box>
                                    <Box
                                        component="form"
                                        sx={{
                                            width: '94%',
                                            paddingBottom: '20px',
                                            paddingTop: '20px',
                                            '& .MuiTextField-root': { m: 1, width: '94%' }
                                        }}
                                        noValidate
                                        autoComplete="off"
                                    >
                                        <div className="rr-section-list-items">
                                            <Typography variant="h5" sx={{
                                                paddingBottom: '10px',
                                                paddingLeft: '8px'
                                            }}>Description</Typography>

                                            <ul>
                                                {sectionItem.items && sectionItem.items.map((item, itemIndex) => (
                                                    <li key={sectionIndex + "-" + itemIndex}>
                                                        <span>{item}</span>
                                                        <CancelSharpIcon
                                                            className="rr-svg-delete-icon"
                                                            color="error"
                                                            onClick={() => handleDeleteListItem(null, itemIndex, `${cvSection}-${sectionIndex}`)}
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
                                                name={`${cvSection}-items-list-${sectionIndex}`}
                                                onKeyPress={(event) =>
                                                    handleAddListItem(event, `${cvSection}-${sectionIndex}`)}
                                                variant="standard"
                                            />
                                        </div>
                                    </Box>
                                </Grid>
                            </CardContent>
                        </Card>
                        <Divider className="rr-section-divider"/>
                    </div>
                )
            });
            if (elements.length > 0) {
                return <>{elements}</>
            }
        }
        return (
            <div className="rr-section-placeholder"/>
        );
    }

    const buildSkillsSection = (cvSection: string): JSX.Element => {
        if (props.editorContent) {
            const elements = props.editorContent[cvSection].map((sectionItem, sectionIndex) => {
                const parentKey = cvSection + "-form-" + sectionIndex;
                let institutionHeader = "Title";
                let institutionField = "heading";
                return (
                    <div key={parentKey}>
                        <Card>
                            <CardHeader
                                action={
                                    <Button
                                        onClick={() => handleDelete(`${cvSection}-${sectionIndex}`)}
                                        className="rr-section-control-btn"
                                        startIcon={<DeleteIcon />}
                                    >
                                        Delete
                                    </Button>
                                }
                            />
                            <CardContent>
                                <Grid item xs={12}>
                                    <Box
                                        component="form"
                                        sx={{
                                            '& .MuiTextField-root': { m: 1, width: '94%' },
                                        }}
                                        noValidate
                                        autoComplete="off"
                                    >

                                        <div>
                                            <TextField
                                                id="standard-institution"
                                                label={institutionHeader}
                                                value={sanitize(sectionItem[institutionField])}
                                                InputLabelProps={{
                                                    shrink: true,
                                                }}
                                                name={`${cvSection}-${institutionField}-${sectionIndex}`}
                                                onChange={handleChange}
                                                variant="standard"
                                            />
                                        </div>
                                    </Box>
                                    <Box
                                        component="form"
                                        sx={{
                                            width: '94%',
                                            paddingBottom: '20px',
                                            paddingTop: '20px',
                                            '& .MuiTextField-root': { m: 1, width: '94%' }
                                        }}
                                        noValidate
                                        autoComplete="off"
                                    >
                                        <div className="rr-section-list-items">
                                            <Typography variant="h5" sx={{
                                                paddingBottom: '10px',
                                                paddingLeft: '8px'
                                            }}>Description</Typography>

                                            <ul>
                                                {sectionItem.items && sectionItem.items.map((item, itemIndex) => (
                                                    <li key={sectionIndex + "-" + itemIndex}>
                                                        <span>{item}</span>
                                                        <CancelSharpIcon
                                                            className="rr-svg-delete-icon"
                                                            color="error"
                                                            onClick={() => handleDeleteListItem(null, itemIndex, `${cvSection}-${sectionIndex}`)}
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
                                                name={`${cvSection}-items-list-${sectionIndex}`}
                                                onKeyPress={(event) =>
                                                    handleAddListItem(event, `${cvSection}-${sectionIndex}`)}
                                                variant="standard"
                                            />
                                        </div>
                                    </Box>
                                </Grid>
                            </CardContent>
                        </Card>
                        <Divider className="rr-section-divider"/>
                    </div>
                )
            });
            if (elements.length > 0) {
                return <>{elements}</>
            }
        }
        return (
            <div className="rr-section-placeholder"/>
        );
    }

    const buildSocialsSection = (cvSection: string): JSX.Element => {
        if (props.editorContent) {
            const elements = props.editorContent[cvSection].map((sectionItem, sectionIndex) => {
                const parentKey = cvSection + "-form-" + sectionIndex;
                return (
                    <div key={parentKey}>
                        <Card>
                            <CardHeader
                                action={
                                    <Button
                                        onClick={() => handleDelete(`${cvSection}-${sectionIndex}`)}
                                        className="rr-section-control-btn"
                                        startIcon={<DeleteIcon />}
                                    >
                                        Delete
                                    </Button>
                                }
                            />
                            <CardContent>
                                <Grid item xs={12}>
                                    <Box
                                        component="form"
                                        sx={{
                                            '& .MuiTextField-root': { m: 1, width: '94%' },
                                        }}
                                        noValidate
                                        autoComplete="off"
                                    >

                                        <div>
                                            <TextField
                                                id="standard-title"
                                                label="Title"
                                                value={sanitize(sectionItem.name)}
                                                InputLabelProps={{
                                                    shrink: true,
                                                }}
                                                name={`${cvSection}-name-${sectionIndex}`}
                                                onChange={handleChange}
                                                variant="standard"
                                            />
                                        </div>
                                        <div>
                                            <TextField
                                                id="standard-handle"
                                                label="Handle"
                                                value={sanitize(sectionItem.handle)}
                                                InputLabelProps={{
                                                    shrink: true,
                                                }}
                                                name={`${cvSection}-handle-${sectionIndex}`}
                                                onChange={handleChange}
                                                variant="standard"
                                            />
                                            <TextField
                                                id="standard-link"
                                                label="Link"
                                                value={sanitize(sectionItem.url)}
                                                InputLabelProps={{
                                                    shrink: true,
                                                }}
                                                name={`${cvSection}-url-${sectionIndex}`}
                                                onChange={handleChange}
                                                variant="standard"
                                            />
                                        </div>
                                    </Box>
                                    <Box
                                        component="form"
                                        sx={{
                                            width: '94%',
                                            paddingBottom: '20px',
                                            paddingTop: '20px',
                                            '& .MuiTextField-root': { m: 1, width: '94%' }
                                        }}
                                        noValidate
                                        autoComplete="off"
                                    >
                                    </Box>
                                </Grid>
                            </CardContent>
                        </Card>
                        <Divider className="rr-section-divider"/>
                    </div>
                )
            });
            if (elements.length > 0) {
                return <>{elements}</>
            }
        }
        return (
            <div className="rr-section-placeholder"/>
        );
    }

    const buildPersonalProfile = (): JSX.Element => {
        if (props.editorContent && props.editorContent.profile) {
            const profile = props.editorContent.profile
            const parentKey = "profile-form-";
            return (
                <div key={parentKey}>
                    <Grid item xs={12}>
                        <Box
                            component="form"
                            sx={{
                                '& .MuiTextField-root': {m: 1, width: '46%'},
                            }}
                            noValidate
                            autoComplete="off"
                        >
                            <div>
                                <TextField
                                    id="standard-first-name"
                                    label="First Name"
                                    value={sanitize(profile.first_name)}
                                    InputLabelProps={{
                                        shrink: true,
                                    }}
                                    name="first_name"
                                    onChange={handleProfileChange}
                                    variant="standard"
                                />
                                <TextField
                                    id="standard-last-name"
                                    label="Last Name"
                                    value={sanitize(profile.last_name)}
                                    InputLabelProps={{
                                        shrink: true,
                                    }}
                                    name="last_name"
                                    onChange={handleProfileChange}
                                    variant="standard"
                                />
                            </div>
                        </Box>
                        <Box
                            component="form"
                            sx={{
                                '& .MuiTextField-root': {m: 1, width: '94%'},
                            }}
                            noValidate
                            autoComplete="off"
                        >
                            <div>
                                <TextField
                                    id="standard-job-title"
                                    label="Job Title"
                                    value={sanitize(profile.position)}
                                    InputLabelProps={{
                                        shrink: true,
                                    }}
                                    name="position"
                                    onChange={handleProfileChange}
                                    variant="standard"
                                />
                            </div>

                            <div>
                                <TextField
                                    id="standard-address"
                                    label="Address"
                                    value={sanitize(profile.location)}
                                    InputLabelProps={{
                                        shrink: true,
                                    }}
                                    name="location"
                                    onChange={handleProfileChange}
                                    variant="standard"
                                />
                            </div>
                        </Box>
                        <Box
                            component="form"
                            sx={{
                                '& .MuiTextField-root': {m: 1, width: '25ch'},
                            }}
                            noValidate
                            autoComplete="off"
                        >
                            <div>
                                <TextField
                                    id="standard-mobile"
                                    label="Mobile"
                                    value={sanitize(profile.mobile)}
                                    InputLabelProps={{
                                        shrink: true,
                                    }}
                                    name="mobile"
                                    onChange={handleProfileChange}
                                    variant="standard"
                                />
                                <TextField
                                    id="standard-email"
                                    label="Mobile"
                                    value={sanitize(profile.email)}
                                    InputLabelProps={{
                                        shrink: true,
                                    }}
                                    name="email"
                                    onChange={handleProfileChange}
                                    variant="standard"
                                />
                            </div>
                        </Box>
                    </Grid>
                    <Divider className="rr-section-divider"/>
                </div>
            )
        }
        return null;
    }

    return (
        <>
        {
            props.editorContent &&
                <Box
                    className="rr-cv-editor"
                    sx={{
                        paddingTop: '5rem',
                        paddingBottom: '5px',
                        paddingLeft: '2rem',
                        paddingRight: '2rem',
                        margin: 'auto',
                        display: 'flex',
                        maxHeight: '100vh',
                        overflow: 'auto'
                    }}>
                    <Grid
                        container
                        direction="row"
                        justifyContent="center"
                        alignItems="stretch"
                        spacing={0}
                    >
                        <Card className="rr-card-margin">
                            <CardContent sx={{
                                padding: '12px',
                                "&:last-child": {
                                    paddingBottom: '8px'
                                }
                            }}>
                                <Accordion>
                                    <AccordionSummary
                                        expandIcon={<ExpandMoreIcon/>}
                                        aria-controls="panel1a-content"
                                        id="panel1a-header"
                                    >
                                        <Typography variant="h4">Personal Information</Typography>
                                    </AccordionSummary>
                                    <AccordionDetails>
                                        {buildPersonalProfile()}
                                    </AccordionDetails>
                                </Accordion>
                            </CardContent>
                        </Card>
                        <Card className="rr-card-margin">
                            <CardContent sx={{
                                padding: '12px',
                                "&:last-child": {
                                    paddingBottom: '8px'
                                }
                            }}>
                                <Accordion>
                                    <AccordionSummary
                                        expandIcon={<ExpandMoreIcon/>}
                                        aria-controls="panel1a-content"
                                        id="panel1a-header"
                                    >
                                        <Typography variant="h4" className="rr-section-header">Socials</Typography>
                                    </AccordionSummary>
                                    <AccordionDetails>
                                        {buildSocialsSection("socials")}
                                        <Button
                                            onClick={() => handleInsert("socials")}
                                            className="rr-section-control-btn"
                                            color="success"
                                            startIcon={<AddIcon/>}
                                        >
                                            Add
                                        </Button>
                                    </AccordionDetails>
                                </Accordion>
                            </CardContent>
                        </Card>
                        <Card className="rr-card-margin">
                            <CardContent sx={{
                                padding: '12px',
                                "&:last-child": {
                                    paddingBottom: '8px'
                                }
                            }}>
                                <Accordion>
                                    <AccordionSummary
                                        expandIcon={<ExpandMoreIcon/>}
                                        aria-controls="panel1a-content"
                                        id="panel1a-header"
                                    >
                                        <Typography variant="h4"
                                                    className="rr-section-header">Experience</Typography>
                                    </AccordionSummary>
                                    <AccordionDetails>
                                        {buildGenericFormSection("work_experience")}
                                        <Button
                                            onClick={() => handleInsert("work_experience")}
                                            className="rr-section-control-btn"
                                            color="success"
                                            startIcon={<AddIcon/>}
                                        >
                                            Add
                                        </Button>
                                    </AccordionDetails>
                                </Accordion>
                            </CardContent>
                        </Card>
                        <Card className="rr-card-margin">
                            <CardContent sx={{
                                padding: '12px',
                                "&:last-child": {
                                    paddingBottom: '8px'
                                }
                            }}>
                                <Accordion>
                                    <AccordionSummary
                                        expandIcon={<ExpandMoreIcon/>}
                                        aria-controls="panel1a-content"
                                        id="panel1a-header"
                                    >
                                        <Typography variant="h4"
                                                    className="rr-section-header">Education</Typography>
                                    </AccordionSummary>
                                    <AccordionDetails>
                                        {buildGenericFormSection("education")}
                                        <Button
                                            onClick={() => handleInsert("education")}
                                            className="rr-section-control-btn"
                                            color="success"
                                            startIcon={<AddIcon/>}
                                        >
                                            Add
                                        </Button>
                                    </AccordionDetails>
                                </Accordion>
                            </CardContent>
                        </Card>
                        <Card className="rr-card-margin">
                            <CardContent sx={{
                                padding: '12px',
                                "&:last-child": {
                                    paddingBottom: '8px'
                                }
                            }}>
                                <Accordion>
                                    <AccordionSummary
                                        expandIcon={<ExpandMoreIcon/>}
                                        aria-controls="panel1a-content"
                                        id="panel1a-header"
                                    >
                                        <Typography variant="h4" className="rr-section-header">Projects</Typography>
                                    </AccordionSummary>
                                    <AccordionDetails>
                                        {buildGenericFormSection("projects")}
                                        <Button
                                            onClick={() => handleInsert("projects")}
                                            className="rr-section-control-btn"
                                            color="success"
                                            startIcon={<AddIcon/>}
                                        >
                                            Add
                                        </Button>
                                    </AccordionDetails>
                                </Accordion>
                            </CardContent>
                        </Card>
                        <Card className="rr-card-margin">
                            <CardContent sx={{
                                padding: '12px',
                                "&:last-child": {
                                    paddingBottom: '8px'
                                }
                            }}>
                                <Accordion>
                                    <AccordionSummary
                                        expandIcon={<ExpandMoreIcon/>}
                                        aria-controls="panel1a-content"
                                        id="panel1a-header"
                                    >
                                        <Typography variant="h4"
                                                    className="rr-section-header">Extracurriculars</Typography>
                                    </AccordionSummary>
                                    <AccordionDetails>
                                        {buildGenericFormSection("extracurriculars")}
                                        <Button
                                            onClick={() => handleInsert("extracurriculars")}
                                            className="rr-section-control-btn"
                                            color="success"
                                            startIcon={<AddIcon/>}
                                        >
                                            Add
                                        </Button>
                                    </AccordionDetails>
                                </Accordion>
                            </CardContent>
                        </Card>
                        <Card className="rr-card-margin">
                            <CardContent sx={{
                                padding: '12px',
                                "&:last-child": {
                                    paddingBottom: '8px'
                                }
                            }}>
                                <Accordion>
                                    <AccordionSummary
                                        expandIcon={<ExpandMoreIcon/>}
                                        aria-controls="panel1a-content"
                                        id="panel1a-header"
                                    >
                                        <Typography variant="h4" className="rr-section-header">Skills</Typography>
                                    </AccordionSummary>
                                    <AccordionDetails>
                                        {buildSkillsSection("skills")}
                                        <Button
                                            onClick={() => handleInsert("skills")}
                                            className="rr-section-control-btn"
                                            color="success"
                                            startIcon={<AddIcon/>}
                                        >
                                            Add
                                        </Button>
                                    </AccordionDetails>
                                </Accordion>
                            </CardContent>
                        </Card>
                    </Grid>
                </Box>
        }
        </>
    );
};
export default ResumeFormEditor;