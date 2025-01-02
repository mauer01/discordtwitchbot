# DiscordTwitchBot

A simple Java-based Discord bot integrated with Twitch functionality. This bot utilizes the [JDA (Java Discord API)](https://github.com/DV8FromTheWorld/JDA) for interacting with Discord and connects to Twitch for additional features.

## Features
- Responds to Discord messages with custom reactions.
- Monitors Twitch streams using provided credentials.
- Configurable status and activity for the bot.

---

## Prerequisites
### Tools and Libraries:
- **Java Development Kit (JDK)**: Ensure you have JDK 8 or higher installed.
- **Maven**: Build and manage your project dependencies.
- **[JDA](https://github.com/DV8FromTheWorld/JDA)**: Discord Java library for bot development.
- **[dotenv](https://github.com/cdimascio/java-dotenv)**: Library for managing environment variables.

### Accounts and Credentials:
1. **Discord Bot Token**
   - Create a bot on the [Discord Developer Portal](https://discord.com/developers/applications).
   - Add the bot to your server with appropriate permissions.
2. **Twitch Application Credentials**
   - Create a Twitch application via the [Twitch Developer Portal](https://dev.twitch.tv/).
   - Obtain your `Client ID` and `Client Secret`.

---

## Installation
1. Clone the repository:
   ```bash
   git clone <repository-url>
   cd DiscordTwitchBot
   ```

2. Install dependencies using Maven:
   ```bash
   mvn clean install
   ```

3. Set up environment variables:
   - Create a `.env` file in the root of the project.
   - Add the following keys:
     ```env
     DISCORD_TOKEN=your_discord_token
     DISCORD_STATUS=your_bot_status
     TWITCH_CLIENT_ID=your_twitch_client_id
     TWITCH_CLIENT_SECRET=your_twitch_client_secret
     ```

---

## Usage
1. Run the bot:
   ```bash
   mvn exec:java -Dexec.mainClass="main.DiscordTwitchBot"
   ```

2. The bot will:
   - Appear online in Discord with the configured status.
   - Start responding to messages as defined in `NachrichtenReaktion`.
   - Interact with Twitch using the `MyTwitch` class (ensure proper implementation).

---

## Project Structure
- `main/`
  - `DiscordTwitchBot`: Main entry point for the application.
  - `NachrichtenReaktion`: Handles Discord message reactions.
  - `Categories`: Manages dynamic bot behavior and parameters.
  - `MyTwitch`: Interacts with Twitch API (implementation required).

---

## Contributing
1. Fork the repository.
2. Create your feature branch (`git checkout -b feature/your-feature`).
3. Commit your changes (`git commit -m 'Add your feature'`).
4. Push to the branch (`git push origin feature/your-feature`).
5. Open a Pull Request.

---

## Acknowledgments
- [JDA](https://github.com/DV8FromTheWorld/JDA) for the powerful Discord API.
- [dotenv](https://github.com/cdimascio/java-dotenv) for environment variable management.
- [Twitch API](https://dev.twitch.tv/docs/api) for stream data and interaction.

