import React, { FC } from "react";
import { DefaultPlayer as Video } from 'react-html5video';
import 'react-html5video/dist/styles.css';
import {Box, Typography} from "@mui/material";

interface VideoElementProps {
    src: string,
    caption?: string
}
'Complete with reusable components, all pages and sections are available in the Figma ecosystem.'
const VideoElement: FC<VideoElementProps> = (props) => {
    return (
        <Box sx={{marginTop: '2rem'}}>
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
                {props.caption}
            </Typography>
            </Box>
        </Box>
    );
}
export default VideoElement;