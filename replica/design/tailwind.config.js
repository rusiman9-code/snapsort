/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    "./src/**/*.{js,ts,jsx,tsx,html}",
    "./components/**/*.{js,ts,jsx,tsx}"
  ],
  theme: {
    extend: {
      colors: {
        bg: "var(--color-bg)",
        surface: {
          DEFAULT: "var(--color-surface)",
          elevated: "var(--color-surface-elevated)"
        },
        border: {
          DEFAULT: "var(--color-border)",
          input: "var(--color-border-input)"
        },
        text: {
          DEFAULT: "var(--color-text)",
          muted: "var(--color-text-muted)"
        },
        accent: {
          DEFAULT: "var(--color-accent)",
          subtle: "var(--color-accent-subtle)"
        },
        "on-accent": "var(--color-on-accent)",
        danger: {
          DEFAULT: "var(--color-danger)"
        },
        "on-danger": "var(--color-on-danger)",
        success: {
          DEFAULT: "var(--color-success)"
        },
        "on-success": "var(--color-on-success)",
        warning: {
          DEFAULT: "var(--color-warning)"
        },
        "on-warning": "var(--color-on-warning)"
      },
      fontFamily: {
        sans: ["var(--font-sans)"],
        mono: ["var(--font-mono)"]
      },
      borderRadius: {
        xs: "var(--radius-xs)",
        sm: "var(--radius-sm)",
        md: "var(--radius-md)",
        lg: "var(--radius-lg)",
        xl: "var(--radius-xl)",
        pill: "var(--radius-pill)"
      },
      boxShadow: {
        subtle: "var(--shadow-subtle)",
        card: "var(--shadow-card)",
        floating: "var(--shadow-floating)",
        sheet: "var(--shadow-sheet)"
      },
      transitionTimingFunction: {
        standard: "var(--ease-standard)"
      }
    }
  },
  plugins: []
};
