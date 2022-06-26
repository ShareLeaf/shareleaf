import {Configuration} from "../api";

export const headerConfig = () => {
    const config = new Configuration();
    config.baseOptions = {
        headers: { ContentType: "application/json" },
    };
    return config
}