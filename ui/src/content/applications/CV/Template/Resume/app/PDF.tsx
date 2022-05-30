import {
    Document,
    Page,
    View,
    StyleSheet,
    Font
} from "@react-pdf/renderer";
import * as React from "react";
import {FC, useEffect, useState} from "react";
import {
    EducationPost,
    Heading,
    IconName,
    Insight,
    Language,
    ListItem,
    Resume,
    Section, SocialMedia, TechGroup,
    Watermark,
    WorkPost
} from "./ui";
import {TechProfile, TechResume} from "../types";
import {RRResume} from "../../../../../../api";
import { observer } from "mobx-react";
import {useStore} from "../../../../../../hooks/useStore";


const styles = StyleSheet.create({
    document: {
        backgroundColor: 'red',
    },
    page: {
        paddingTop: 48,
        paddingHorizontal: 50,
        // backgroundColor: 'red',
        // padding: 10,
        // fontSize: 10,
    },
    row: {
        flexDirection: 'row',
    },
    leftColumn: {
        flexGrow: 1,
        marginRight: 16,
        width: '55%',
    },
    rightColumn: {
        flexGrow: 1,
        width: '40%',
    },
})

interface PDFProps {
    techProfile: TechProfile
    techResume: TechResume,
    resume: RRResume
}
const PDF: FC<PDFProps> = observer(({ techProfile, techResume, resume }) => {
        return (
        <Document
            title={`${techProfile.name} - ${techResume.title}`}
            author={techProfile.name}
            keywords={techResume.keywords}
        >
            <Page size="LETTER" style={styles.page}>
                <Watermark/>
                <Heading
                    title={`${resume.profile.first_name}  ${resume.profile.last_name}`}
                    subtitle={techResume.title}
                    avatarUrl={techResume.avatarUrl}
                    information={{
                        phone: techProfile.phone,
                        email: techProfile.email,
                        website: techProfile.website,
                        location: techProfile.location,
                    }}
                />
                <View style={styles.row}>
                    <View style={styles.leftColumn}>
                        <Section title="Work Experience" spacing={8}>
                            {techResume.workExperiences.map((workExperience) => (
                                <WorkPost
                                    key={workExperience.id}
                                    title={workExperience.title}
                                    companyName={workExperience.company}
                                    companyUrl={workExperience.companyUrl}
                                    location={workExperience.location}
                                    startAt={workExperience.startAt}
                                    endAt={workExperience.endAt}
                                    description={workExperience.description}
                                >
                                    {workExperience.lines
                                        ?.split('\n')
                                        .filter(Boolean)
                                        .map((line, i) => (
                                            <ListItem key={`${workExperience.id}-line-${i}`}>
                                                {line}
                                            </ListItem>
                                        ))}
                                </WorkPost>
                            ))}
                        </Section>
                    </View>
                    <View style={styles.rightColumn}>
                        <Section title="Introduction" spacing={8}>
                            <Resume>{techResume.aboutMe}</Resume>
                        </Section>
                        <Section title="Education" spacing={8}>
                            {techResume.educationExperiences.map((educationExperience) => (
                                <EducationPost
                                    key={educationExperience.id}
                                    title={educationExperience.title}
                                    almaMater={educationExperience.almaMater}
                                    startAt={educationExperience.startAt}
                                    endAt={educationExperience.endAt}
                                    location={educationExperience.location}
                                >
                                    {educationExperience.lines
                                        .split('\n')
                                        .filter(Boolean)
                                        .map((line, i) => (
                                            <ListItem key={`${educationExperience.id}-line-${i}`}>
                                                {line}
                                            </ListItem>
                                        ))}
                                </EducationPost>
                            ))}
                        </Section>
                        <Section title="Languages" spacing={12}>
                            {techResume.langSkills.map((langSkill) => (
                                <Language
                                    key={langSkill.id}
                                    name={langSkill.name}
                                    scoreLabel={langSkill.scoreLabel}
                                    score={langSkill.score}
                                />
                            ))}
                        </Section>
                        <Section title="Strengths" spacing={12}>
                            {techResume.strengths.map((strength) => (
                                <Insight
                                    key={strength.id}
                                    title={strength.name}
                                    description={strength.description}
                                    iconName={strength.icon as IconName}
                                />
                            ))}
                        </Section>
                        <Section title="Socials" spacing={0}>
                            <View style={{flexDirection: 'row'}}>
                                <SocialMedia
                                    name="Twitter"
                                    profileUrl={techProfile.twitter}
                                    style={{flex: 1}}
                                />
                                <SocialMedia
                                    name="Github"
                                    profileUrl={techProfile.github}
                                    style={{flex: 1}}
                                />
                                <SocialMedia
                                    name="LinkedIn"
                                    profileUrl={techProfile.linkedin}
                                    style={{flex: 1}}
                                />
                            </View>
                        </Section>
                    </View>
                </View>
            </Page>
            {techResume.workProjects?.length ? (
                <Page size="A4" style={styles.page}>
                    <Watermark/>
                    <View style={styles.row}>
                        <View style={styles.leftColumn}>
                            <Section title="Last Projects" spacing={8}>
                                {techResume.workProjects?.map((workProject) => (
                                    <WorkPost
                                        key={workProject.id}
                                        title={workProject.title}
                                        location={workProject.location}
                                        companyName={workProject.company}
                                        startAt={workProject.startAt}
                                        endAt={workProject.endAt}
                                        description={workProject.description}
                                    >
                                        {workProject.lines
                                            ?.split('\n')
                                            .filter(Boolean)
                                            .map((line, i) => (
                                                <ListItem key={`${workProject.id}-line-${i}`}>
                                                    {line}
                                                </ListItem>
                                            ))}
                                    </WorkPost>
                                ))}
                            </Section>
                        </View>
                        <View style={styles.rightColumn}>
                            <Section title="Technologies" spacing={8}>
                                {techResume.techGroups?.map((techGroup) => (
                                    <TechGroup
                                        key={techGroup.id}
                                        title={techGroup.title}
                                        tags={techGroup.tags}
                                    />
                                ))}
                            </Section>
                        </View>
                    </View>
                </Page>
            ) : null}
        </Document>
    );

})
export default PDF;