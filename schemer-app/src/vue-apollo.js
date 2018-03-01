import Vue from 'vue';
import VueApollo from 'vue-apollo';
import createApolloClient from './apollo';

Vue.use(VueApollo);

const options = {
  endpoints: {
    graphql: '/graphql',
  },
};

// Create apollo client
export const apolloClient = createApolloClient(options);

// Create vue apollo provider
export const apolloProvider = new VueApollo({
  defaultClient: apolloClient,
});
