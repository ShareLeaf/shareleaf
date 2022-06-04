import dynamic from 'next/dynamic';

import PageHeader from '@/content/Dashboards/Fitness/PageHeader';
import PageTitleWrapper from 'src/components/PageTitleWrapper';
import Footer from 'src/components/Footer';

import { Grid } from '@mui/material';

import Steps from '@/content/Dashboards/Fitness/Steps';
import Energy from '@/content/Dashboards/Fitness/Energy';
import Water from '@/content/Dashboards/Fitness/Water';
import Calories from '@/content/Dashboards/Fitness/Calories';
import Activity from '@/content/Dashboards/Fitness/Activity';
import UpcomingEvents from '@/content/Dashboards/Fitness/UpcomingEvents';
import ProfileGoals from '@/content/Dashboards/Fitness/ProfileGoals';
import CaloriesAlt from '@/content/Dashboards/Fitness/CaloriesAlt';
import ProteinAlt from '@/content/Dashboards/Fitness/ProteinAlt';
import CarbsAlt from '@/content/Dashboards/Fitness/CarbsAlt';
import FatAlt from '@/content/Dashboards/Fitness/FatAlt';
import Parameters from '@/content/Dashboards/Fitness/Parameters';

const TrainingPrograms = dynamic(
  () => import('@/content/Dashboards/Fitness/TrainingPrograms'),
  { ssr: true }
);

const MonthlyGoalsTarget = dynamic(
  () => import('@/content/Dashboards/Fitness/MonthlyGoalsTarget'),
  { ssr: true }
);

function DashboardFitnessContent() {
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
        <Grid item lg={6} xs={12}>
          <Grid
            container
            direction="row"
            justifyContent="center"
            alignItems="stretch"
            spacing={3}
          >
            <Grid item sm={6} md={3} lg={6} xs={12}>
              <Steps />
            </Grid>
            <Grid item sm={6} md={3} lg={6} xs={12}>
              <Energy />
            </Grid>
            <Grid item sm={6} md={3} lg={6} xs={12}>
              <Water />
            </Grid>
            <Grid item sm={6} md={3} lg={6} xs={12}>
              <Calories />
            </Grid>
          </Grid>
        </Grid>
        <Grid item lg={6} xs={12}>
          <Activity />
        </Grid>
        <Grid item xs={12}>
          <TrainingPrograms />
        </Grid>
        <Grid item md={6} xs={12}>
          <UpcomingEvents />
        </Grid>
        <Grid item md={6} xs={12}>
          <ProfileGoals />
        </Grid>
        <Grid item md={6} xs={12}>
          <MonthlyGoalsTarget />
        </Grid>
        <Grid item md={6} xs={12}>
          <Grid
            container
            direction="row"
            justifyContent="center"
            alignItems="stretch"
            spacing={3}
          >
            <Grid item sm={6} xs={12}>
              <CaloriesAlt />
            </Grid>
            <Grid item sm={6} xs={12}>
              <ProteinAlt />
            </Grid>
            <Grid item sm={6} xs={12}>
              <CarbsAlt />
            </Grid>
            <Grid item sm={6} xs={12}>
              <FatAlt />
            </Grid>
          </Grid>
        </Grid>
        <Grid item xs={12}>
          <Parameters />
        </Grid>
      </Grid>

      <Footer />
    </>
  );
}

export default DashboardFitnessContent;
