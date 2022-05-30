import {useTheme} from "@mui/material/styles";
import { Text, View, StyleSheet } from "@react-pdf/renderer";

export interface TechGroupProps {
  style?: any
  title: string
  tags: string[]
}

const styles = StyleSheet.create({
  title: {
    fontSize: 12,
    fontFamily: 'Quicksand',
    fontWeight: 'bold',
    marginBottom: 4,
  },
  tag: {
    fontSize: 9,
    fontFamily: 'OpenSans',
  },
  tagContainer: {
    borderStyle: 'solid',
    borderWidth: 1,
    padding: 4,
    marginRight: 6,
    marginBottom: 6,
  },
  tagsContainer: {
    flexDirection: 'row',
    flexWrap: 'wrap',
  },
})

export function TechGroup({ style, title, tags }: TechGroupProps) {
  const theme = useTheme()

  return (
    <View style={style}>
      <Text style={[styles.title, { color: theme.palette.error.main }]}>
        {title}
      </Text>
      <View style={styles.tagsContainer}>
        {tags.map((tag) => (
          <View
            key={tag}
            style={[styles.tagContainer, { borderColor: theme.palette.grey["300"] }]}
          >
            <Text style={styles.tag}>{tag}</Text>
          </View>
        ))}
      </View>
    </View>
  )
}
