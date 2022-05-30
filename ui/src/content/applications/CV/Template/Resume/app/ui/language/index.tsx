import {useTheme} from "@mui/material/styles";
import { View, Text, StyleSheet } from "@react-pdf/renderer";
import { Score } from '../score'

export interface LanguageProps {
  style?: any
  name: string
  scoreLabel: string
  score: number
}

const styles = StyleSheet.create({
  container: {
    flexDirection: 'row',
    alignItems: 'center',
  },
  name: {
    fontFamily: 'OpenSans',
    fontSize: 10,
  },
  scoreLabel: {
    fontFamily: 'OpenSans',
    fontSize: 8,
    marginLeft: 'auto',
    marginRight: 5,
  },
})

export function Language({ style, name, scoreLabel, score }: LanguageProps) {
  const theme = useTheme()

  return (
    <View style={[styles.container, style!]}>
      <Text style={styles.name}>{name}</Text>
      <Text style={[styles.scoreLabel, { color: theme.palette.grey["900"] }]}>
        {scoreLabel}
      </Text>
      <Score id="language" rank={score} />
    </View>
  )
}
