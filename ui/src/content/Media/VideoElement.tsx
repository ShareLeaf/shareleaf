import React, {FC, useRef} from "react";
import {Box, Typography} from "@mui/material";
import VideoJS from './VideoJS'
import {VideoJsPlayer} from 'video.js';

interface VideoElementProps {
    encoding: string
    videoSrc: string,
    audioSrc?: string,
    title?: string,
    thumbnail?: string
}
const VideoElement: FC<VideoElementProps> = (props) => {
    const playerRef = useRef<VideoJsPlayer>(null);
    const enableVolumeControl = props.audioSrc && props.audioSrc.length > 0 ? true : false; // do not simplify
    const videoJsOptions = {
        loop: true,
        autoplay: true,
        controls: true,
        responsive: true,
        fluid: true,
        muted: true,
        preload: "metadata",
        sources: [{
            src: props.videoSrc + "#t=0.1",
            type: 'video/mp4'
        }],
        controlBar: {
            playToggle: true,
            pictureInPictureToggle: false,
            progressControl: true,
            fullscreenToggle: true,
            volumePanel: enableVolumeControl,
            remainingTimeDisplay: true,
        }
    };

    const handlePlayerReady = (player: VideoJsPlayer) => {
        playerRef.current = player;

        player.on('seeked', () => {
            handleAudioSync(player);
        });

        player.on('pause', () => {
            handleAudioSync(player);
        });

        player.on('playing', () => {
            handleAudioSync(player);
        });

        player.on('play', () => {
            handleAudioSync(player);
        });

        player.on('volumechange', () => {
            handleAudioSync(player);
        });
    };

    const handleAudioSync = (player: VideoJsPlayer) => {
        const audio: HTMLAudioElement = getAudioComponent();
        try {
            audio.currentTime = player.currentTime();
            audio.volume = player.volume();
            if (player.muted()) {
                audio.volume = 0;
            }
            if (player.paused()) {
                audio.pause();
            } else {
                audio.play()
                    .then(_ => {
                    })
                    .catch(_ => {
                    });
            }
        } catch (_) {}
    }

    const getAudioComponent = (): HTMLAudioElement => {
        // @ts-ignore
        return document.getElementById("audio-track");
    }

    return (
        <Box sx={{marginTop: '4.8rem', width: '100%'}}>
            <VideoJS options={videoJsOptions} onReady={handlePlayerReady} audioSrc={props.audioSrc}/>
            <Box sx={{
                pl: 1,
                pr: 1
            }}>
            <Typography
                sx={{
                    pt: 2,
                    pb: 2,
                    textAlign: 'left',
                    color: '#000',
                }}
                variant="h4"
            >
                {props.title}
            </Typography>
            </Box>
        </Box>
    );
}
export default VideoElement;

