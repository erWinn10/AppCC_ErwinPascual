const express = require("express")
const mysql = require("mysql2")
const bodyParser = require("body-parser")

const app = express()
const PUERTO = 1027

app.use(bodyParser.json())

const conexion = mysql.createConnection(
    {
        host:'localhost',
        database:'appcocobolov2',
        user:'root',
        password:'Galletita.com1'
    }
)

app.listen(PUERTO, () =>{
    console.log("Servidor corriendo en el puerto " + PUERTO)
})

conexion.connect(error =>{
    if(error) throw error
    console.log("Conexión existosa a la base de datos")
})

app.get("/", (req, res)=>{
    res.send("Bienvenido al servicio web de Cocobolo & Cocobaby")
})

app.get("/coleccion", (req, res)=>{
    const consulta = "SELECT * FROM  coleccion";
    conexion.query(consulta, (error, rpta)=>{
        if(error) return console.error(error.message)

        const obj = {}
        if(rpta.length > 0){
            obj.lstColeccion = rpta
            res.json(obj)
        }else{
            res.json("No hay registros")
        }
    })
})

/*app.get("/articulo", (req, res)=>{
    const consulta = "SELECT * FROM  articulo";
    conexion.query(consulta, (error, rpta)=>{
        if(error) return console.error(error.message)

        const obj = {}
        if(rpta.length > 0){
            obj.lstArticulo = rpta
            res.json(obj)
        }else{
            res.json("No hay registros")
        }
    })
})*/

app.get("/articulo", (req, res) => {
    const consulta = "SELECT * FROM articulo";
    conexion.query(consulta, (error, rpta) => {
        if (error) return console.error(error.message);

        const obj = {};
        if (rpta.length > 0) {
            // Convertir las fechas de cadena a objetos Date
            rpta.forEach(articulo => {
                articulo.fecha_registro = new Date(articulo.fecha_registro);
            });
            obj.lstArticulo = rpta;
            res.json(obj);
        } else {
            res.json("No hay registros");
        }
    });
});


app.get("/roles", (req, res)=>{
    const consulta = "SELECT * FROM  roles";
    conexion.query(consulta, (error, rpta)=>{
        if(error) return console.error(error.message)

        const obj = {}
        if(rpta.length > 0){
            obj.lstRoles = rpta
            res.json(obj)
        }else{
            res.json("No hay registros")
        }
    })
})

app.get("/usuarios", (req, res)=>{
    const consulta = "SELECT * FROM  usuarios";
    conexion.query(consulta, (error, rpta)=>{
        if(error) return console.error(error.message)

        const obj = {}
        if(rpta.length > 0){
            obj.lstUsuarios = rpta
            res.json(obj)
        }else{
            res.json("No hay registros")
        }
    })
})

app.get("/pedidos", (req, res)=>{
    const consulta = "SELECT * FROM  pedido";
    conexion.query(consulta, (error, rpta)=>{
        if(error) return console.error(error.message)

        const obj = {}
        if(rpta.length > 0){
            obj.lstPedidos = rpta
            res.json(obj)
        }else{
            res.json("No hay registros")
        }
    })
})

app.post("/usuarios/agregar", (req, res) =>{
    const usuarios = {
        usu_id: req.body.usu_id,
        usu_nombre: req.body.usu_nombre,
        usu_correo: req.body.usu_correo,
        usu_contraseña: req.body.usu_contraseña,
        rol_id: req.body.rol_id
    }
    const consulta = "INSERT INTO usuarios SET ?"
    conexion.query(consulta, usuarios, (error) =>{
        if(error) return console.error(error.message)
            else res.json("Se insertó correctamente el usuario")
    })
})

/*app.get("/usuarios/:correo", (req, res) => {
    const { correo } = req.params;

    // Validar el formato del correo (opcional)
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(correo)) {
        return res.status(400).json("Ingrese un correo válido");
    }

    const consulta = "SELECT * FROM usuarios WHERE usu_correo = ?";
    conexion.query(consulta, [correo], (error, respuesta) => {
        if (error) {
            console.error(error.message);
            return res.status(500).json("Error en el servidor");
        }
        if (respuesta.length > 0) {
            // Mensaje cuando se encuentra el usuario
            res.json({
                mensaje: "Usuario encontrado",
                usuario: respuesta[0]
            });
        } else {
            // Mensaje cuando no se encuentra el usuario
            res.status(404).json("No se encontró el usuario con ese correo");
        }
    });*/


    app.post("/usuarios/login", (req, res) => {
        const { correo, contraseña } = req.body; // Cambia a req.body para recibir datos en el cuerpo de la solicitud
    
        // Validar el formato del correo
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (!emailRegex.test(correo)) {
            return res.status(400).json("Ingrese un correo válido");
        }
    
        // Validar que la contraseña no esté vacía
        if (!contraseña) {
            return res.status(400).json("Ingrese su contraseña");
        }
    
        const consulta = "SELECT * FROM usuarios WHERE usu_correo = ? AND usu_contraseña = ?";
        conexion.query(consulta, [correo, contraseña], (error, respuesta) => {
            if (error) {
                console.error(error.message);
                return res.status(500).json("Error en el servidor");
            }
            if (respuesta.length > 0) {
                // Mensaje cuando se encuentra el usuario
                res.json({
                    mensaje: "Inicio de sesión exitoso",
                    usuario: respuesta[0]
                });
            } else {
                // Mensaje cuando no se encuentra el usuario
                res.status(404).json("Credenciales incorrectas");
            }
        });
    });
    

    app.post("/pedidos/agregar", (req, res) =>{
        const pedidos = {
            ped_id: req.body.ped_id,
            ped_cantidad: req.body.ped_cantidad,
            ped_fecha_registro: req.body.ped_fecha_registro,
            art_id: req.body.art_id,
            usu_id: req.body.usu_id
        }
        const consulta = "INSERT INTO pedido SET ?"
        conexion.query(consulta, pedidos, (error) =>{
            if(error) return console.error(error.message)
                else res.json("Se registró el pedido correctamente")
        })
    })

    app.post("/coleccion/agregar", (req, res) =>{
        const coleccion = {
            col_id: req.body.col_id,
            col_nombre: req.body.col_nombre,
            col_descripcion: req.body.col_descripcion,
            col_imagenref: req.body.col_imagenref,
        }
        const consulta = "INSERT INTO coleccion SET ?"
        conexion.query(consulta, coleccion, (error) =>{
            if(error) return console.error(error.message)
                else res.json("Se insertó correctamente la colección")
        })
    })

    app.post("/articulo/agregar", (req, res) =>{
        const articulo = {
            art_id: req.body.art_id,
            art_nombre: req.body.art_nombre,
            art_precio: req.body.art_precio,
            art_fecha_registro: req.body.art_fecha_registro,
            art_imagen: req.body.art_imagen,
            col_id: req.body.col_id,
        }
        const consulta = "INSERT INTO articulo SET ?"
        conexion.query(consulta, articulo, (error) =>{
            if(error) return console.error(error.message)
                else res.json("Se insertó correctamente el artículo")
        })
    })

    app.delete("/pedidos/eliminar/:id", (req, res) =>{
        const {id} = req.params

        const consulta = "DELETE FROM pedido WHERE ped_id="+id

        conexion.query(consulta, (error) =>{
            if(error) return console.error(error.message)
                else res.json("Se eliminó el pedido correctamente")
        })
    })

    app.delete("/usuarios/eliminar/:id", (req, res) =>{
        const {id} = req.params

        const consulta = "DELETE FROM usuarios WHERE usu_id="+id

        conexion.query(consulta, (error) =>{
            if(error) return console.error(error.message)
                else res.json("Se eliminó el usuario correctamente")
        })
    })

    app.delete("/coleccion/eliminar/:id", (req, res) =>{
        const {id} = req.params

        const consulta = "DELETE FROM coleccion WHERE col_id="+id

        conexion.query(consulta, (error) =>{
            if(error) return console.error(error.message)
                else res.json("Se eliminó la coleccion correctamente")
        })
    })

    app.delete("/articulo/eliminar/:id", (req, res) =>{
        const {id} = req.params

        const consulta = "DELETE FROM articulo WHERE art_id="+id

        conexion.query(consulta, (error) =>{
            if(error) return console.error(error.message)
                else res.json("Se eliminó el articulo correctamente")
        })
    })

    app.put("/coleccion/modificar/:id", (req, res)=>{
        const {id} = req.params
        const {col_nombre, col_descripcion, col_imagenref} = req.body
    
        const consulta = "UPDATE coleccion SET col_nombre = '"+ col_nombre +
                            "', col_descripcion = '"+ col_descripcion +
                            "', col_imagenref = '"+ col_imagenref +
                            "' WHERE col_id = "+ id
    
        conexion.query(consulta, (error)=>{
            if(error) return console.error(error.message)
                res.json("Se actualizó correctamente la coleccion")
        })
    })