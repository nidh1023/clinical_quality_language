package org.cqframework.cql.web;

import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;
import org.cqframework.cql.cql2elm.CqlCompilerException;
import org.cqframework.cql.cql2elm.CqlCompilerOptions;
import org.cqframework.cql.cql2elm.CqlTranslator;
import org.cqframework.cql.cql2elm.LibraryManager;
import org.cqframework.cql.cql2elm.ModelManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CqlWebServer {

    public static void main(String[] args) {
        int port = args.length > 0 ? Integer.parseInt(args[0]) : 8080;

        Javalin app = Javalin.create(config -> {
            config.staticFiles.add("/static", Location.CLASSPATH);
        });

        app.post("/translate", ctx -> {
            Map<?, ?> body = ctx.bodyAsClass(Map.class);
            String cql = body.get("cql") != null ? body.get("cql").toString() : "";

            ModelManager modelManager = new ModelManager();
            LibraryManager libraryManager = new LibraryManager(modelManager,
                    CqlCompilerOptions.defaultOptions());

            CqlTranslator translator = CqlTranslator.fromText(cql, libraryManager);

            List<Map<String, String>> errors = new ArrayList<>();
            for (CqlCompilerException e : translator.getExceptions()) {
                Map<String, String> entry = new HashMap<>();
                entry.put("severity", e.getSeverity().toString());
                entry.put("message", e.getMessage() != null ? e.getMessage() : "");
                if (e.getLocator() != null) {
                    entry.put("line", String.valueOf(e.getLocator().getStartLine()));
                    entry.put("char", String.valueOf(e.getLocator().getStartChar()));
                } else {
                    entry.put("line", "");
                    entry.put("char", "");
                }
                errors.add(entry);
            }

            boolean hasErrors = translator.getErrors() != null && !translator.getErrors().isEmpty();

            Map<String, Object> result = new HashMap<>();
            result.put("elm", hasErrors ? "" : translator.toJson());
            result.put("errors", errors);

            ctx.json(result);
        });

        app.start(port);
        System.out.println("CQL Web UI running at http://localhost:" + port);
    }
}
