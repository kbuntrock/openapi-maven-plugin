import type {ReactNode} from 'react';
import clsx from 'clsx';
import Heading from '@theme/Heading';
import styles from './styles.module.css';
import useBaseUrl from '@docusaurus/useBaseUrl';

import Translate, {translate} from '@docusaurus/Translate';

type FeatureItem = {
  title: string;
  imageUrl: string;
  description: ReactNode;
};

const FeatureList: FeatureItem[] = [
    {
        title:
            translate({
                message: 'Hybrid Analysis Approach',
            }),
        imageUrl: '/img/flower_small.png',
        description: (
            <>
              <Translate>Uses both compiled classes and source code to enrich the generated specification: extracts Javadoc comments directly, without extra annotations.</Translate>
            </>
        ),
    },
    {
        title:
            translate({
                message: 'Highly configurable',
            }),
        imageUrl: '/img/toolbox_small.png',
        description: (
            <>
                <Translate>Numerous options to fine-tune the generated documentation: whitelists / blacklists, multiple documentation generation, default errors, ...</Translate>
            </>
        ),
    },
    {
      title:
          translate({
            message: 'Fast and flexible',
          })
      ,
      imageUrl: '/img/gym_small.png',
      description: (
        <>
          <Translate>Works faster than methods requiring a running application or integration test execution. Documentation can be generated from modules that only contain interfaces.</Translate>
        </>
      ),
    },
    {
      title:
        translate({
          message: 'Lightweight & Secure',
        }),
      imageUrl: '/img/padlock_small.png',
      description: (
        <>
          <Translate>No extra dependencies added to your JAR/WAR. Reduces the surface for security vulnerabilities compared to other methods.</Translate>
        </>
      ),
    },
    {
        title:
            translate({
                message: 'Broad compatibility',
            }),
        imageUrl: '/img/puzzle_small.png',
        description: (
            <>
                <Translate>Works with Spring MVC, Jakarta RS and Javax RS. Verified with JDK 8, 11, 17, and 21.</Translate>
            </>
        ),
    },
    {
        title:
            translate({
                message: 'Open source & free',
            }),
        imageUrl: '/img/owl_small.png',
        description: (
            <>
              <Translate>Missing a feature? Add it to the project! 😉</Translate>
            </>
        ),
    },
];

function Feature({title, imageUrl, description}: FeatureItem) {
  return (
    <div className={clsx('col col--4')}>
      <div className="text--center">
        <img className={styles.featureImg} src={useBaseUrl(imageUrl)}/>
      </div>
      <div className="text--center padding-horiz--md">
        <Heading as="h3">{title}</Heading>
        <p>{description}</p>
      </div>
    </div>
  );
}

export default function HomepageFeatures(): ReactNode {
  return (
    <section className={styles.features}>
      <div className="container">
        <div className="row">
          {FeatureList.map((props, idx) => (
            <Feature key={idx} {...props} />
          ))}
        </div>
      </div>
    </section>
  );
}
