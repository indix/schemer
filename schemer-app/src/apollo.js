import { ApolloClient } from 'apollo-client';
import { HttpLink } from 'apollo-link-http';
import { InMemoryCache } from 'apollo-cache-inmemory';

// Create the apollo client
export default ({ endpoints }) => {
  const httpLink = new HttpLink({
    uri: endpoints.graphql,
  });

  // Apollo cache
  const cache = new InMemoryCache();
  const apolloClient = new ApolloClient({
    link: httpLink,
    cache,
    connectToDevTools: process.env.NODE_ENV !== 'production',
  });

  return apolloClient;
};
