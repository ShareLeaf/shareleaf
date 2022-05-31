import React, { FC } from "react";
import { DefaultPlayer as Video } from 'react-html5video';
import 'react-html5video/dist/styles.css';
import {Box, Typography} from "@mui/material";

interface MediaProps {
    src: string,
    type: string
}

const Media: FC<MediaProps> = (props) => {
    if (props.type === "video") {
        return (
            <Box sx={{
                marginTop: '2rem'
            }}>
                <Video autoPlay loop muted
                       controls={['PlayPause', 'Seek', 'Time', 'Volume', 'Fullscreen']}
                       poster="http://sourceposter.jpg"
                       onCanPlayThrough={() => {
                           // Do stuff
                       }}>
                    <source src="http://media.w3.org/2010/05/sintel/trailer.mp4" type="video/webm" />
                    <track label="English" kind="subtitles" srcLang="en" src="http://source.vtt" default />
                </Video>
                <Box sx={{
                    marginTop: '0.5rem'
                }}>
                <Typography
                    sx={{
                        pt: 1
                    }}
                    variant="h3"
                >
                    {
                        'Complete with reusable components, all pages and sections are available in the Figma ecosystem.'
                    }
                </Typography>
                </Box>
            </Box>
        );
    }

    return <></>

}
export default Media;