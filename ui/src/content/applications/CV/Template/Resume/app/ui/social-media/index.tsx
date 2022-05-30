import { Text, StyleSheet, View, Link } from"@react-pdf/renderer";
import {useTheme} from "@mui/material/styles";

export interface SocialMediaProps {
  name: string
  profileUrl: string
  style?: any
}

const styles = StyleSheet.create({
  name: {
    fontFamily: 'OpenSans',
    fontSize: 10,
  },
  username: {
    fontFamily: 'OpenSans',
    fontSize: 8,
    marginTop: 2,
  },
})

export function SocialMedia({ name, profileUrl, ...props }: SocialMediaProps) {
  const theme = useTheme()

  const username = `@${profileUrl.split('/').splice(-1)}`

  return (
    <View {...props}>
      <Text style={styles.name}>{name}</Text>
      <Link src={profileUrl}>
        <Text style={[styles.username, { color: theme.palette.grey["900"] }]}>
          {username}
        </Text>
      </Link>
    </View>
  )
}
