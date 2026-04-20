import { PrismTheme, themes as prismThemes } from "prism-react-renderer";

const _darkTheme: PrismTheme = {
  ...prismThemes.oneDark,
};

_darkTheme.styles.push({
  types: ["token", "annotation", "punctuation"],
  style: {
    color: "hsl(41, 63%, 40%)",
  },
});

const _lightTheme: PrismTheme = {
  ...prismThemes.oneLight,
};

_lightTheme.styles.push({
  types: ["token", "annotation", "punctuation"],
  style: {
    color: "hsl(41, 63%, 40%)",
  },
});

export const lightTheme = _lightTheme;

export const darkTheme = _darkTheme;
