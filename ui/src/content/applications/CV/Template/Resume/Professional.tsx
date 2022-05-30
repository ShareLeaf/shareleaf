import * as React from "react";
import {
    RREducation,
    RRResume,
    RRSkills,
    RRSocial
} from "../../../../../api";
import moment from "moment";
import {getFormattedDate} from "../date";

const getLocation = (location: string, url: string): string => {
    let combinedLocation = location;
    if (url) {
        if (combinedLocation) {
            combinedLocation = combinedLocation + " | " + url;
        } else {
            combinedLocation = url;
        }
    }
    return combinedLocation;
}
interface ResumeProps {
    content: RRResume
}

export class Professional extends React.Component<ResumeProps> {
    constructor(props: any) {
        super(props);
        this.getSocials = this.getSocials.bind(this);
        this.getGenericCVSection = this.getGenericCVSection.bind(this);
        this.getSkills = this.getSkills.bind(this);
        this.getEducation = this.getEducation.bind(this);
        this.getFooter = this.getFooter.bind(this);
        this.getSocialIconClass = this.getSocialIconClass.bind(this);
    }

    getSocialIconClass = (name: string): string => {
        let iconClass = "fas fa-link";
        if (name) {
            name = name.toLowerCase();
            if (name === "github"
                || name === "twitter"
                || name === "linkedin"
                || name === "reddit") { iconClass = "fab fa-" + name}
        }
        return iconClass;
    }

    getFooter = (rrResume: RRResume) : JSX.Element => {
        const date = moment().format("MMMM DD, YYYY")
        return (
            <footer>
                <span className="footer_date">{date}</span>
                <span className="footer_text">
                    {rrResume.profile.first_name + "" + rrResume.profile.last_name} · Résumé
                </span>
            </footer>
        )
    }

    getSocials = (socials: Array<RRSocial>) : JSX.Element => {
        socials = socials.filter(item => item.name && item.url);
        const elements = socials.map((social, index) => {
            return (
                <h6 key={index}>
                    <a href={social.url}> <i className={this.getSocialIconClass(social.name)}/>{social.url.toLowerCase()}</a>
                </h6>)
        })
        return <>{elements}</>
    }

    getGenericCVSection = (cvSection: string, content: RRResume) : JSX.Element => {
        // Do not render a section unless it has at least the name of the institution
        const sectionItems = content[cvSection].filter(item => item.institution || item.position || item.title || item.heading);
        const elements = sectionItems.map((sectionItem, index) => {
            const dateRange = getFormattedDate(sectionItem.start_date) + " - " + getFormattedDate(sectionItem.end_date);
            const location = getLocation(sectionItem.location, sectionItem.url);
            let title = sectionItem.institution;
            if (sectionItem.title) { title = sectionItem.title}
            return (
                <div className="section_item" key={index}>
                    <div>
                        <h4 className="education_institution section_subheading">{title}</h4>
                        {location &&
                            <h4 className="section_location">{location}</h4>
                        }
                    </div>
                    <div>
                        <h5 className="section_subsubheading">{sectionItem.position}</h5>
                        <h5 className="section_subsubheading">{sectionItem.description}</h5>
                        <h5 className="section_date-range">
                            {sectionItem.start_date && dateRange}
                        </h5>
                    </div>
                    <ul>
                        {
                            sectionItem.items && sectionItem.items.map((item, itemIndex) => {
                                const childKey = `${cvSection}-item-${itemIndex}`
                                return <li key={childKey}>{item}</li>
                            })
                        }
                    </ul>
                    <br></br>
                </div>
            )
        })
        if (elements.length > 0) {
            return <>{elements}</>
        }
        return null;
    }

    getSkills = (skills: Array<RRSkills>) : JSX.Element => {
        skills = skills.filter(item => item.heading);
        const elements = skills.map((skill, index) => {
            const parentKey = "skills-section-item-" + index
            return (
                    <tr key={parentKey}>
                        <td className="skills_heading">{skill.heading}</td>
                        <td className="skills_list">
                            {
                                skill.items && skill.items.map((item, itemIndex) => {
                                    const childKey = "skills-section-item-list-" + itemIndex;
                                    return <span key={childKey}>{item}</span>
                                })
                            }
                        </td>
                    </tr>
            )
        })
        if (elements.length > 0) {
            return <table><tbody>{elements}</tbody></table>
        }
        return null;
    }

    getEducation = (rrEducations: Array<RREducation>) : JSX.Element => {
        rrEducations = rrEducations.filter(item => item.institution);
        const elements = rrEducations.map((education, index) => {
            const parentKey = "education-section-" + index;
            let majorAndGpa = "";
            if (education.major && education.gpa) { majorAndGpa = education.major + " " + education.gpa.toString(); }
            else if (education.major) { majorAndGpa = education.major; }
            else if (education.gpa) { majorAndGpa =  education.gpa.toString(); }
            const dateRange = getFormattedDate(education.start_date) + " - " + getFormattedDate(education.end_date);
            let location = getLocation(education.location, education.url);
            return (
                <div className="section_item" key={parentKey}>
                    <div>
                        <h4 className="education_institution section_subheading">{education.institution}</h4>
                        {location &&
                            <h4 className="section_location">{location}</h4>
                        }
                    </div>
                    <div>
                        <h5 className="section_subsubheading">{majorAndGpa}</h5>
                        <h5 className="section_date-range">
                            {education.start_date && dateRange}
                        </h5>
                    </div>
                    <ul>
                        {
                            education.items && education.items.map((item, itemIndex) => {
                                const childKey = "education-item-" + itemIndex
                                return <li key={childKey}>{item}</li>
                            })
                        }
                    </ul>
                    <br></br>
                </div>
            )
        })
        if (elements.length > 0) {
            return <>{elements}</>
        }
        return null;
    }


    render() {
        const content: RRResume = this.props.content
        const educationBlock = this.getEducation(content.education);
        const experienceBlock = this.getGenericCVSection("work_experience", content);
        const projectsBlock = this.getGenericCVSection("projects", content);
        const extracurricularsBlock = this.getGenericCVSection("extracurriculars", content);
        const skillsBlock = this.getSkills(content.skills);

        return (
            <div id="document">
            <div className="page">
                <div className="about-me">
                    <h1 className="about-me_name">{content.profile.first_name + " "}
                        <span className="about-me_last-name">{content.profile.last_name}</span></h1>
                    <h6 className="about-me_position">{content.profile.position}</h6>
                    <h6 className="about-me_address">{content.profile.location}</h6>
                    <span className="about-me_social">
                                            <h6><a href={"tel:" + content.profile.mobile}>
                                                <i className="fa fa-phone"></i>{content.profile.mobile}</a></h6>
                                            <h6>
                                                <a href={"mailto:" + content.profile.email}>
                                                <i className="fa fa-envelope"></i>{content.profile.email}</a>
                                            </h6>
                        {this.getSocials(content.socials)}
                                        </span>
                </div>

                {experienceBlock &&
                    <section className="experience">
                        <div className="section_heading">
                            <h1>Experience</h1>
                            <span className="section_heading-underline"></span>
                        </div>
                        {experienceBlock}
                    </section>
                }

                {educationBlock &&
                    <section className="education">
                        <div className="section_heading">
                            <h1>Education</h1>
                            <span className="section_heading-underline"></span>
                        </div>
                        {educationBlock}
                    </section>
                }
                {projectsBlock &&
                    <section className="projects">
                        <div className="section_heading">
                            <h1>Projects</h1>
                            <span className="section_heading-underline"></span>
                        </div>
                        {projectsBlock}
                    </section>
                }

                {extracurricularsBlock &&
                    <section className="extracurricular">
                        <div className="section_heading">
                            <h1>Extracurricular Activity</h1>
                            <span className="section_heading-underline"></span>
                        </div>
                        {extracurricularsBlock}
                    </section>
                }

                {skillsBlock &&
                    <section className="skills">
                        <div className="section_heading">
                            <h1>Skills</h1>
                            <span className="section_heading-underline"></span>
                        </div>
                        {skillsBlock}
                    </section>
                }

                {this.getFooter(content)}
            </div>
        </div>
        );
    }
}



