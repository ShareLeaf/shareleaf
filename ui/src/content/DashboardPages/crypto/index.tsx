import PageHeader from '@/content/Dashboards/Crypto/PageHeader';
import PageTitleWrapper from 'src/components/PageTitleWrapper';
import Footer from 'src/components/Footer';

import { Grid } from '@mui/material';

import AccountBalance from '@/content/Dashboards/Crypto/AccountBalance';
import Wallets from '@/content/Dashboards/Crypto/Wallets';
import AccountSecurity from '@/content/Dashboards/Crypto/AccountSecurity';
import RecentOrders from '@/content/Dashboards/Crypto/RecentOrders';
import WatchList from '@/content/Dashboards/Crypto/WatchList';

function DashboardCryptoContent() {
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
          <AccountBalance />
        </Grid>
        <Grid item lg={8} xs={12}>
          <Wallets />
        </Grid>
        <Grid item lg={4} xs={12}>
          <AccountSecurity />
        </Grid>
        <Grid item xs={12}>
          <RecentOrders />
        </Grid>
        <Grid item xs={12}>
          <WatchList />
        </Grid>
      </Grid>

      <Footer />
    </>
  );
}

export default DashboardCryptoContent;
