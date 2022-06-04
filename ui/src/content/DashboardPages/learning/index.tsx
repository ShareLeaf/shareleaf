import dynamic from 'next/dynamic';

import PageHeader from '@/content/Dashboards/Learning/PageHeader';
import Footer from 'src/components/Footer';
import PageTitleWrapper from 'src/components/PageTitleWrapper';

import { Grid } from '@mui/material';

import TimeSpent from '@/content/Dashboards/Learning/TimeSpent';
import TopTrainers from '@/content/Dashboards/Learning/TopTrainers';
import Leaderboard from '@/content/Dashboards/Learning/Leaderboard';
import RecentCourses from '@/content/Dashboards/Learning/RecentCourses';

const UpcomingConferences = dynamic(
  () => import('@/content/Dashboards/Learning/UpcomingConferences'),
  { ssr: true }
);

function DashboardLearningContent() {
  return (
    <>
      <PageTitleWrapper>
        <PageHeader />
      </PageTitleWrapper>

      <Grid
        sx={{ px: 4 }}
        container
        direction="row"
        justifyContent="center"
        alignItems="stretch"
        spacing={3}
      >
        <Grid item xs={12}>
          <TimeSpent />
        </Grid>
        <Grid item xs={12} md={6}>
          <TopTrainers />
        </Grid>
        <Grid item xs={12} md={6}>
          <Leaderboard />
        </Grid>
        <Grid item xs={12}>
          <UpcomingConferences />
        </Grid>
        <Grid item xs={12}>
          <RecentCourses />
        </Grid>
      </Grid>

      <Footer />
    </>
  );
}

export default DashboardLearningContent;
