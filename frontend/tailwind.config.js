import aspectRatio from "@tailwindcss/aspect-ratio";
import animate from "tailwindcss-animate";

export default {
  darkMode: ["class"],
  content: ["./src/**/*.{js,ts,jsx,tsx,mdx}"],
  theme: {
    extend: {
      colors: {
        grayscale: {
          100: "#ECEFF3",
          200: "#D3D5DA",
          400: "#A3A5AB",
          600: "#6B6E75",
          800: "#3A3D44",
          black: "#1D1F24",
          white: "#FFFFFF",
        },
        system: {
          success: "#429482",
          error: "#E55D57",
        },
        primary: "#003399",
        secondary: {
          blue: "#459CED",
          pink: "#D377F3",
        },
        additional: {
          deepblue: "#222683",
          lightblue: "#4A86E4",
          coral: "#EF746D",
          green: "#5D923D",
        },
      },
      backgroundColor: {
        DEFAULT: "#ECEFF3",
        white: "#FFFFFF",
        primary: "#003399",
        gray: "#D3D5DA",
        card: "#F6F7FA",
        tag: "#E2F3F0",
        redtag: "#F9E8E7",
        system: {
          success: "#429482",
          error: "#E55D57",
        },
      },
      borderColor: {
        DEFAULT: "#D3D5DA",
        primary: "#003399",
      },
      borderRadius: {
        lg: "20px",
        md: "12px",
        sm: "8px",
      },
      fontFamily: {
        spoqa: ["Spoqa Han Sans Neo", "sans-serif"],
      },
      fontWeight: {
        thin: "100",
        extralight: "200",
        light: "300",
        normal: "400",
        medium: "500",
        semibold: "600",
        bold: "700",
        extrabold: "800",
        black: "900",
      },
      fontSize: {
        xl: "24px",
        lg: "20px",
        md: "18px",
        mmd: "16px",
        sm: "14px",
        xs: "12px",
      },
      transitionDuration: {
        DEFAULT: "300ms",
        fast: "150ms",
        slow: "500ms",
      },
    },
  },
  plugins: [animate, aspectRatio],
};
