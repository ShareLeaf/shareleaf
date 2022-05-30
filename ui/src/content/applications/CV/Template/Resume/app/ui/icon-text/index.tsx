import {useTheme} from "@mui/material/styles";
import { Text, View, StyleSheet } from "@react-pdf/renderer";
import { Icon, IconName } from '../icon'

export interface IconTextProps {
  style?: any
  text: string
  iconName: IconName
}

const styles = StyleSheet.create({
  container: {
    flexDirection: 'row',
    alignItems: 'center',
  },
  text: {
    fontFamily: 'Quicksand',
    fontSize: 8,
    marginLeft: 4,
  },
})

export function IconText({ style, text, iconName }: IconTextProps) {
  const theme = useTheme()

  return (
    <View style={[styles.container, style!]}>
      <Icon size={10} name={iconName} />
      <Text style={[styles.text, { color: theme.palette.grey["900"] }]}>{text}</Text>
    </View>
  )
}
