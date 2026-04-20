<#import "template.ftl" as layout>
<@layout.emailLayout>
<table width="100%" cellpadding="0" cellspacing="0" border="0" style="font-family: Arial, Helvetica, sans-serif; background:#f6f8fb; padding:0; margin:0;">
  <tr>
    <td align="center" style="padding: 32px 16px;">
      <table width="100%" cellpadding="0" cellspacing="0" border="0" style="max-width: 640px; background:#ffffff; border-radius: 14px; overflow:hidden; border:1px solid #e8edf3;">
        <tr>
          <td style="background:#2b59ff; padding: 28px 32px; color:#ffffff;">
            <div style="font-size: 13px; text-transform: uppercase; letter-spacing: .08em; opacity:.85;">Welcome</div>
            <div style="font-size: 28px; font-weight: 700; line-height: 1.2; margin-top: 8px;">Добро пожаловать, ${username?html}</div>
          </td>
        </tr>

        <tr>
          <td style="padding: 32px;">
            <p style="margin:0 0 16px 0; font-size: 15px; line-height: 1.7; color:#1f2937;">
              Аккаунт создан для <strong>${email?html}</strong>.
            </p>

            <p style="margin:0 0 24px 0; font-size: 15px; line-height: 1.7; color:#374151;">
              Чтобы завершить настройку и продолжить работу, нажмите кнопку ниже.
            </p>

            <table cellpadding="0" cellspacing="0" border="0" style="margin: 0 0 24px 0;">
              <tr>
                <td bgcolor="#2b59ff" style="border-radius:10px;">
                  <a href="${link?html}" target="_blank"
                     style="display:inline-block; padding:14px 24px; color:#ffffff; text-decoration:none; font-weight:700; font-size:15px;">
                    Continue
                  </a>
                </td>
              </tr>
            </table>

            <p style="margin:0 0 8px 0; font-size: 13px; line-height: 1.6; color:#6b7280;">
              Если кнопка не открывается, используйте ссылку вручную:
            </p>

            <p style="margin:0; font-size: 13px; line-height: 1.6; word-break: break-word;">
              <a href="${link?html}" target="_blank" style="color:#2b59ff;">${link?html}</a>
            </p>
          </td>
        </tr>
      </table>
    </td>
  </tr>
</table>
</@layout.emailLayout>