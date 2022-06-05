import React, {FC, useEffect, useRef} from "react";
import {Box, Typography} from "@mui/material";

interface VideoElementProps {
    encoding: string
    src: string,
    title?: string
}
const VideoElement: FC<VideoElementProps> = (props) => {
    const videoRef = useRef(undefined);
    useEffect(() => {
        videoRef.current.defaultMuted = true;
    })

    return (
        <Box sx={{marginTop: '2rem'}}>
            <video
                ref={videoRef}
                className="sl-video-player"
                controls
                autoPlay
                loop
                playsInline
                muted width={"100%"}>
                <source src={props.src} type="video/mp4"/>
            </video>
            <Box sx={{
                marginTop: '0.5rem'
            }}>
            <Typography
                sx={{
                    pt: 1,
                    textAlign: 'left'
                }}
                variant="h3"
            >
                {props.title}
            </Typography>
            </Box>
        </Box>
    );
}
export default VideoElement;
