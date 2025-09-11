import {themes as prismThemes} from 'prism-react-renderer';
import type {Config} from '@docusaurus/types';
import type * as Preset from '@docusaurus/preset-classic';
import type {PluginsParams} from 'svgo/plugins/plugins-types';
import * as path from "node:path";

// This runs in Node.js - Don't use client-side code here (browser APIs, JSX...)

const config: Config = {
  title: 'OpenAPI Maven Plugin',
  tagline: 'Easily turn your Java APIs into OpenAPI documentation.',
  favicon: 'img/favicon.ico',

  // Future flags, see https://docusaurus.io/docs/api/docusaurus-config#future
  future: {
    v4: true, // Improve compatibility with the upcoming Docusaurus v4
  },

  // Set the production url of your site here
  url: 'https://kbuntrock.github.io',
  // Set the /<baseUrl>/ pathname under which your site is served
  // For GitHub pages deployment, it is often '/<projectName>/'
  baseUrl: '/openapi-maven-plugin/',

  // GitHub pages deployment config.
  // If you aren't using GitHub pages, you don't need these.
  organizationName: 'github.com/kbuntrock', // Usually your GitHub org/user name.
  projectName: 'openapi-maven-plugin', // Usually your repo name.
  deploymentBranch: 'docs',

  onBrokenLinks: 'throw',
  onBrokenMarkdownLinks: 'warn',

  // Even if you don't use internationalization, you can use this field to set
  // useful metadata like html lang. For example, if your site is Chinese, you
  // may want to replace "en" with "zh-Hans".
  i18n: {
    defaultLocale: 'en',
    locales: ['en', 'fr'],
  },

  presets: [
    [
      'classic',
      {
        docs: {
          sidebarPath: './sidebars.ts',
          // Please change this to your repo.
          // Remove this to remove the "edit this page" links.
          // editUrl:
          //   'https://github.com/facebook/docusaurus/tree/main/packages/create-docusaurus/templates/shared/',
        },
        blog: {
          showReadingTime: true,
          feedOptions: {
            type: ['rss', 'atom'],
            xslt: true,
          },
          // Please change this to your repo.
          // Remove this to remove the "edit this page" links.
          // editUrl:
          //   'https://github.com/facebook/docusaurus/tree/main/packages/create-docusaurus/templates/shared/',
          // Useful options to enforce blogging best practices
          onInlineTags: 'warn',
          onInlineAuthors: 'warn',
          onUntruncatedBlogPosts: 'warn',
        },
        theme: {
          customCss: './src/css/custom.css',
        },
        // This part in only here to resolve inline SVG clash:
        // https://github.com/facebook/docusaurus/issues/8297
        // https://github.com/facebook/docusaurus/issues/10679
        svgr: {
          svgrConfig: {
            svgoConfig: {
              plugins: [
                {
                  name: 'preset-default',
                  params: {
                    overrides: {
                      removeTitle: false,
                      removeViewBox: false,
                    },
                  },
                },
                {
                  name: 'prefixIds',
                  params: {
                    delim: '',
                    cleanupIDs: false,
                    // prefix: (_, info) => path.parse(info.path!).name,
                  } as PluginsParams['prefixIds'],
                },
              ],
            }
          }
        }
      } satisfies Preset.Options,
    ],
  ],

  themes: [
    [
      "@easyops-cn/docusaurus-search-local",
      /** @type {import("@easyops-cn/docusaurus-search-local").PluginOptions} */
      ({
        hashed: true,
        language: ["en", "zh"],
        highlightSearchTermsOnTargetPage: true,
        explicitSearchResultPath: true,
      }),
    ],
  ],
  themeConfig: {
    // Replace with your project's social card
    image: 'img/social-media-card.jpg',
    navbar: {
      title: 'Openapi Maven Plugin',
      logo: {
        alt: 'Openapi Maven Plugin logo',
        src: 'img/logo.svg',
      },
      items: [
        {
          type: 'docSidebar',
          sidebarId: 'documentationSidebar',
          position: 'left',
          label: 'Documentation',
        },
        // {to: '/blog', label: 'Blog', position: 'left'},
        {
          type: 'localeDropdown',
          position: 'right',
        },
        {
          type: "html",
          value: '<a href="https://github.com/kbuntrock/openapi-maven-plugin" target="_blank" rel="noopener noreferrer" class="navbar__item navbar__link header-github-link" aria-label="GitHub repository"></a>',
          position: "right"
        }
      ],
    },
    colorMode: {
      defaultMode: "light",
      disableSwitch: false,
      respectPrefersColorScheme: true
    },
    footer: {
      style: 'dark',
      links: [
        {
          title: 'Docs',
          items: [
            {
              label: 'Documentation',
              to: '/docs/project',
            },
          ],
        },
        {
          title: 'Community',
          items: [
            {
              label: 'Github discussions',
              href: 'https://github.com/kbuntrock/openapi-maven-plugin/discussions',
            }
          ],
        },
        {
          title: 'More',
          items: [
            // {
            //   label: 'Blog',
            //   to: '/blog',
            // },
            {
              label: 'GitHub',
              href: 'https://github.com/kbuntrock/openapi-maven-plugin',
            },
          ],
        },
      ],
      copyright: `Copyright © ${new Date().getFullYear()} Openapi Maven Plugin`,
    },
    prism: {
      theme: prismThemes.github,
      darkTheme: prismThemes.dracula,
      additionalLanguages: ['java'],
    },
    announcementBar: {
      id: 'github_star',
      content:
          '⭐️ Support the project by giving it a star on <a target="_blank" rel="noopener noreferrer" href="https://github.com/kbuntrock/openapi-maven-plugin">GitHub!</a> ⭐️',
      backgroundColor: '#0067a6',
      textColor: '#FFFFFF',
      isCloseable: true,
    },
  } satisfies Preset.ThemeConfig,
};

export default config;
